package com.checkout.payment.bankcommon.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BankRequest implements Serializable {

  @JsonProperty("card_number")
  private int cardNumber;
  @JsonProperty("expiry_date")
  private int expiryDate;
  @JsonProperty("currency")
  private String currency;
  @JsonProperty("amount")
  private int amount;
  // 3-4 characters long and numeric characters only
  @JsonProperty("cvv")
  private String cvv;

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumber=" + cardNumber +
        ", expiryDate=" + expiryDate +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
