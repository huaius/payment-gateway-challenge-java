package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.bankclient.AcquiringBankClient;
import com.checkout.payment.gatewaycommon.model.PaymentStatus;
import com.checkout.payment.gatewaycommon.model.PostPaymentRequest;
import com.checkout.payment.gatewaycommon.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {
  final private static String USD = "USD";

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;
  @Autowired
  AcquiringBankClient acquiringBankClient;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = PostPaymentResponse.builder()
        .id(UUID.randomUUID())
        .amount(10)
        .cardNumberLastFour(4321)
        .expiryMonth(12)
        .expiryYear(2024)
        .currency("USD")
        .status(PaymentStatus.AUTHORIZED)
        .build();

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenProcessPaymentWithValidInfoThenCorrectPaymentIsReturned() throws Exception {
    final PostPaymentRequest request = createPostPaymentRequest();
    final String content = new ObjectMapper().writeValueAsString(request);

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(6789))
        .andExpect(jsonPath("$.expiryMonth").value(request.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(request.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(request.getCurrency()))
        .andExpect(jsonPath("$.amount").value(request.getAmount()));
  }

  PostPaymentRequest createPostPaymentRequest() {
    return PostPaymentRequest.builder()
        .cardNumber("123456789")
        .amount(10)
        .cvv("1234")
        .currency(USD)
        .expiryMonth(12)
        .expiryYear(2030)
        .build();
  }
}
