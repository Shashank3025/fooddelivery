package com.fooddelivery.payment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fooddelivery.payment.entity.Payment;

@Component
public class PaymentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentEvent(Payment payment) {
        String event = "PaymentSuccess:" + payment.getOrderId() + ":" + payment.getAmount();
        kafkaTemplate.send("payment-events", String.valueOf(payment.getOrderId()), event);
        System.out.println("📤 Sent payment event: " + event);
    }
}