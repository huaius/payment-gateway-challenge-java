package com.checkout.payment.bankclient;

import java.util.UUID;

import com.checkout.payment.bankcommon.model.BankRequest;
import com.checkout.payment.bankcommon.model.BankResponse;
import com.checkout.payment.bankcommon.model.ServiceUnavailableException;

public class AcquiringBankClient {

  public BankResponse process(final BankRequest bankRequest) {
    final String cardNumber = bankRequest.getCardNumber();

    final int lastDigit = Integer.parseInt(cardNumber.substring(cardNumber.length() - 1));

    if (lastDigit == 0) {
      throw new ServiceUnavailableException("");
    } else {
      boolean authorised = lastDigit % 2 != 0;
      // not authorised if even
      return BankResponse.builder()
          .authorized(authorised)
          .authorizationCode(String.valueOf(UUID.randomUUID()))
          .build();
    }
  }
}
