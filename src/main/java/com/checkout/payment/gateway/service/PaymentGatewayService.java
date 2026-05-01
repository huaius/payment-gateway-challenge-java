package com.checkout.payment.gateway.service;

import com.checkout.payment.bankcommon.model.BankRequest;
import com.checkout.payment.bankcommon.model.BankResponse;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.bankclient.AcquiringBankClient;
import com.checkout.payment.gatewaycommon.model.PaymentStatus;
import com.checkout.payment.gatewaycommon.model.PostPaymentRequest;
import com.checkout.payment.gatewaycommon.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan("com.checkout.payment.bankclient")
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final AcquiringBankClient acquiringBankClient;

  public PaymentGatewayService(PaymentsRepository paymentsRepository,
      AcquiringBankClient acquiringBankClient) {
    this.paymentsRepository = paymentsRepository;
    this.acquiringBankClient = acquiringBankClient;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(final PostPaymentRequest paymentRequest) {
    if (validatePaymentRequest(paymentRequest)) {
      final BankRequest bankRequest = BankRequest.builder()
          .cvv(paymentRequest.getCvv())
          .amount(paymentRequest.getAmount())
          .expiryDate(paymentRequest.getExpiryDate())
          .currency(paymentRequest.getCurrency())
          .cardNumber(paymentRequest.getCardNumber())
          .build();
      final BankResponse bankResponse = acquiringBankClient.process(bankRequest);
      final PostPaymentResponse postPaymentResponse = PostPaymentResponse.builder()
          .id(UUID.randomUUID())
          .status(bankResponse.isAuthorized() ? PaymentStatus.AUTHORIZED: PaymentStatus.DECLINED)
          .amount(paymentRequest.getAmount())
          .expiryMonth(paymentRequest.getExpiryMonth())
          .cardNumberLastFour(getLastFourDigits(paymentRequest.getCardNumber()))
          .currency(paymentRequest.getCurrency())
          .expiryYear(paymentRequest.getExpiryYear())
          .build();
      paymentsRepository.add(postPaymentResponse);
      return postPaymentResponse;
    } else {
      return invalidResponse();
    }
  }

  boolean validatePaymentRequest(final PostPaymentRequest paymentRequest) {
    return true;
  }

  PostPaymentResponse invalidResponse() {
    return PostPaymentResponse.builder()
        .status(PaymentStatus.REJECTED)
        .build();
  }

  int getLastFourDigits(final String cardNumber) {
    return Integer.parseInt(cardNumber.substring(cardNumber.length() - 4));
  }
}
