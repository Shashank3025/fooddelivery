package com.fooddelivery.payment.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
    private Double amount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private String message;
}