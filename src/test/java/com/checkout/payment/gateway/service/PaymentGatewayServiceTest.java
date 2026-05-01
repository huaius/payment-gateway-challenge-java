package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.checkout.payment.bankclient.AcquiringBankClient;
import com.checkout.payment.bankcommon.model.BankRequest;
import com.checkout.payment.bankcommon.model.BankResponse;
import com.checkout.payment.bankcommon.model.ServiceUnavailableException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gatewaycommon.model.PaymentStatus;
import com.checkout.payment.gatewaycommon.model.PostPaymentRequest;
import com.checkout.payment.gatewaycommon.model.PostPaymentResponse;

class PaymentGatewayServiceTest {

  final private static String DUMMY = "DUMMY";

  @Autowired
  private PaymentGatewayService service;

  @BeforeEach
  void setUp() {
    service = new PaymentGatewayService(new PaymentsRepository(), new AcquiringBankClient());
  }

  @Test
  void getPaymentByIdWhenPaymentWithIdDoesNotExistThenThrowsException() {
    assertThrows(EventProcessingException.class, () -> {
      service.getPaymentById(UUID.randomUUID());
    });
  }

  @Test
  void processPaymentWhenOddInputThenReturnsAuthorised() {
    final PostPaymentRequest request = createPostPaymentRequest();
    final PostPaymentResponse response = service.processPayment(request);
    assertNotNull(response.getId());
    assertEquals(PaymentStatus.AUTHORIZED, response.getStatus());
  }

  @Test
  void processPaymentWhenEvenInputThenReturnsDeclined() {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setCardNumber("123456789012344");
    final PostPaymentResponse response = service.processPayment(request);
    assertEquals(PaymentStatus.DECLINED, response.getStatus());
  }

  @Test
  void processPaymentWhenZeroInputThenReturnsDeclined() {
    assertThrows(ServiceUnavailableException.class, () -> {
      final PostPaymentRequest request = createPostPaymentRequest();
      request.setCardNumber("123456789012340");
      service.processPayment(request);
    });
  }

  @Test
  void validatePaymentRequest() {
  }

  PostPaymentRequest createPostPaymentRequest() {
    return PostPaymentRequest.builder()
        .cardNumber("123456789012345")
        .amount(10)
        .cvv("1234")
        .currency("USD")
        .expiryMonth(12)
        .expiryYear(2030)
        .build();
  }
}