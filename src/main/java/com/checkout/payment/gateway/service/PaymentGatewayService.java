package com.checkout.payment.gateway.service;

import com.checkout.payment.bankcommon.model.BankRequest;
import com.checkout.payment.bankcommon.model.BankResponse;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.bankclient.AcquiringBankClient;
import com.checkout.payment.gatewaycommon.model.PaymentStatus;
import com.checkout.payment.gatewaycommon.model.PostPaymentRequest;
import com.checkout.payment.gatewaycommon.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan("com.checkout.payment.bankclient")
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);
  private static final List<String> CURRENCYS = Arrays.asList("USD", "EUR", "GBP");

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

  PostPaymentResponse invalidResponse() {
    return PostPaymentResponse.builder()
        .status(PaymentStatus.REJECTED)
        .build();
  }

  int getLastFourDigits(final String cardNumber) {
    return Integer.parseInt(cardNumber.substring(cardNumber.length() - 4));
  }

  boolean validatePaymentRequest(final PostPaymentRequest request) {
    return validatePaymentRequestCardNumber(request)
        && validatePaymentRequestDate(request)
        && validatePaymentRequestCurrency(request)
        && validatePaymentRequestAmount(request)
        && validatePaymentRequestCvv(request);
  }

  /**
   * Valid card number length is between 14 and 19, all numeric characters.
   * @param request PostPaymentRequest
   * @return boolean
   */
  private boolean validatePaymentRequestCardNumber(final PostPaymentRequest request) {
    String cardNumber = request.getCardNumber();
    if (cardNumber != null && !cardNumber.trim().isEmpty()) {
      cardNumber = cardNumber.trim();
      final int length = cardNumber.length();
      if (length >= 14 && length <= 19) {
        return cardNumber.matches("^[0-9]+$");
      }
    }
    return false;
  }

  /**
   * Ensure the combination of expiry month + year is in the future.
   * @param request PostPaymentRequest
   * @return boolean
   */
  private boolean validatePaymentRequestDate(final PostPaymentRequest request) {
    final int month = request.getExpiryMonth();
    final int year = request.getExpiryYear();
    if (month > 0 && month < 13 && year > 0) {
      final Calendar calendar = Calendar.getInstance();
      final int curYear = calendar.get(Calendar.YEAR);
      final int curMonth = calendar.get(Calendar.MONTH);
      if (year > curYear || (year == curMonth && month > curMonth)) {
        return true;
      }
    }
    return false;
  }

  /**
   * For test purpose only USD, EUR, GBP are considered valid.
   * @param request PostPaymentRequest
   * @return boolean
   */
  private boolean validatePaymentRequestCurrency(final PostPaymentRequest request) {
    final String currency = request.getCurrency();
    return CURRENCYS.contains(currency);
  }

  /**
   * Ensure the amount is bigger than 0.
   * @param request PostPaymentRequest
   * @return boolean
   */
  private boolean validatePaymentRequestAmount(final PostPaymentRequest request) {
    return request.getAmount() > 0;
  }

  /**
   * Ensure 3-4 characters long and numeric characters only.
   * @param request PostPaymentRequest
   * @return boolean
   */
  private boolean validatePaymentRequestCvv(final PostPaymentRequest request) {
    String cvv = request.getCvv();
    if (cvv != null && !cvv.trim().isEmpty()) {
      cvv = cvv.trim();
      final int length = cvv.length();
      if (length >= 3 && length <= 4) {
        return cvv.matches("^[0-9]+$");
      }
    }
    return false;
  }
}
