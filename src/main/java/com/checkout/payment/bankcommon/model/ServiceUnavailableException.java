package com.checkout.payment.bankcommon.model;

public class ServiceUnavailableException extends RuntimeException{
  public ServiceUnavailableException(String message) {
    super(message);
  }
}
