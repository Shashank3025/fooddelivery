package com.fooddelivery.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    private Long orderId;
    private Double amount;
    private String paymentMethod;
    private String idempotencyKey;
}