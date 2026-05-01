package com.checkout.payment.bankclient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.checkout.payment.bankcommon.model.BankRequest;
import com.checkout.payment.bankcommon.model.BankResponse;
import com.checkout.payment.bankcommon.model.ServiceUnavailableException;

class AcquiringBankClientTest {
  final public static String DUMMY = "DUMMY";

  private AcquiringBankClient acquiringBankClient;

  @BeforeEach
  public void setUp() {
    acquiringBankClient = new AcquiringBankClient();
  }

  @Test
  void whenPaymentWithOddThenAuthorisedIsReturned() {
    final BankRequest bankRequest = createBankResponse();
    bankRequest.setCardNumber("1111");
    final BankResponse bankResponse = acquiringBankClient.process(bankRequest);
    assertTrue(bankResponse.isAuthorized());
  }

  @Test
  void whenPaymentWithEvenThenNotAuthorisedIsReturned() {
    final BankRequest bankRequest = createBankResponse();
    bankRequest.setCardNumber("1112");
    final BankResponse bankResponse = acquiringBankClient.process(bankRequest);
    assertFalse(bankResponse.isAuthorized());
  }

  @Test
  void whenPaymentWithOddThenExceptionIsThrown() {
    assertThrows(ServiceUnavailableException.class, () -> {
      final BankRequest bankRequest = createBankResponse();
      bankRequest.setCardNumber("1110");
      final BankResponse bankResponse = acquiringBankClient.process(bankRequest);
      assertTrue(bankResponse.isAuthorized());
    });
  }

  BankRequest createBankResponse() {
    return BankRequest.builder()
        .cvv(DUMMY)
        .expiryDate(DUMMY)
        .amount(0)
        .cardNumber(DUMMY)
        .currency(DUMMY)
        .build();
  }
}