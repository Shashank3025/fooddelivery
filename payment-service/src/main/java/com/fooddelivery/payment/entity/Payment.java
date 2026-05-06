package com.fooddelivery.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Double amount;

    private String status; 
    // INITIATED, PROCESSING, PAID, FAILED, REFUNDED

    private String paymentMethod;
    // CARD, PAYPAL, STRIPE, CASH

    private String transactionId;
    // External payment id from Stripe/PayPal later

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    @Column(unique = true)
    private String idempotencyKey;
}