package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
  void processPaymentWhenZeroInputThenException() {
    assertThrows(ServiceUnavailableException.class, () -> {
      final PostPaymentRequest request = createPostPaymentRequest();
      request.setCardNumber("123456789012340");
      service.processPayment(request);
    });
  }

  @Test
  void processPaymentWhenInvalidInputThenReturnsRejected() {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setCardNumber("123456");
    final PostPaymentResponse response = service.processPayment(request);
    assertEquals(PaymentStatus.REJECTED, response.getStatus());
  }

  @Test
  void validatePaymentRequestWhenValidRequestThenTrue() {
    final PostPaymentRequest request = createPostPaymentRequest();
    assertTrue(service.validatePaymentRequest(request));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "  ",
      "12345",
      "1234567890123",
      "12345678901234567890",
      "1234567890123a"
  })
  void validatePaymentRequestWhenInvalidCardNumberRequestThenFalse(final String cardNumber) {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setCardNumber(cardNumber);
    assertFalse(service.validatePaymentRequest(request));
  }

  @Test
  void validatePaymentRequestWhenNullCardNumberRequestThenFalse() {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setCardNumber(null);
    assertFalse(service.validatePaymentRequest(request));
  }

  @ParameterizedTest
  @ValueSource(ints = {
      0,
      -1,
      13
  })
  void validatePaymentRequestWhenInvalidMonthRequestThenFalse(final int month) {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setExpiryMonth(month);
    assertFalse(service.validatePaymentRequest(request));
  }

  @ParameterizedTest
  @ValueSource(ints = {
      0,
      -1,
      2000
  })
  void validatePaymentRequestWhenInvalidYearRequestThenFalse(final int year) {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setExpiryYear(year);
    assertFalse(service.validatePaymentRequest(request));
  }

  @Test
  void validatePaymentRequestWhenSameMonthYearRequestThenFalse() {
    final Calendar calendar = Calendar.getInstance();
    final int curYear = calendar.get(Calendar.YEAR);
    final int curMonth = calendar.get(Calendar.MONTH);
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setExpiryYear(curYear);
    request.setExpiryMonth(curMonth);
    assertFalse(service.validatePaymentRequest(request));
  }

  @ParameterizedTest
  @ValueSource(ints = {
      0,
      -1
  })
  void validatePaymentRequestWhenInvalidAmountRequestThenFalse(final int number) {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setAmount(number);
    assertFalse(service.validatePaymentRequest(request));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "  ",
      "ABC"
  })
  void validatePaymentRequestWhenInvalidCurrencyRequestThenFalse(final String currency) {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setCurrency(currency);
    assertFalse(service.validatePaymentRequest(request));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "  ",
      "12345",
      "12",
      "123a"
  })
  void validatePaymentRequestWhenInvalidCvvRequestThenFalse(final String cvv) {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setCvv(cvv);
    assertFalse(service.validatePaymentRequest(request));
  }

  @Test
  void validatePaymentRequestWhenNullCvvRequestThenFalse() {
    final PostPaymentRequest request = createPostPaymentRequest();
    request.setCvv(null);
    assertFalse(service.validatePaymentRequest(request));
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