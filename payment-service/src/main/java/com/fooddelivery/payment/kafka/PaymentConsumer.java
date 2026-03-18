package com.fooddelivery.payment.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @KafkaListener(
        topics = "order-events",
        groupId = "payment-group",
        containerFactory = "kafkaListenerContainerFactory" // <-- important
    )
    public void consumeOrderCreated(String message) {
        System.out.println("📩 Received raw Kafka message: " + message);

        // parse order ID
        String[] parts = message.split(":");
        Long orderId = Long.parseLong(parts[1]);

        // simulate payment
        Double amount = 100.0;

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PAID");

        paymentRepository.save(payment);

        System.out.println("✅ Payment processed for order: " + orderId);

        // send payment event
        paymentProducer.sendPaymentEvent(payment);
    }
}