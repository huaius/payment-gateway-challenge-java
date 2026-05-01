package com.checkout.payment.bankcommon.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BankResponse implements Serializable {

  @JsonProperty("authorized")
  private boolean authorized;
  @JsonProperty("authorization_code")
  private String authorizationCode;

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "authorized=" + authorized +
        ", authorization_code=" + authorizationCode +
        '}';
  }
}
