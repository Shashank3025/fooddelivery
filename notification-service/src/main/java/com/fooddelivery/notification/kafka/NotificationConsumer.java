package com.fooddelivery.notification.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics="payment-events", groupId="notification-group")
    public void handlepayment(String message) {
        String notification = "💰 Payment Notification: " + message;

        System.out.println(notification);

        // 🔥 PRODUCE to notification-events
        kafkaTemplate.send("notification-events", notification);
    }

    @KafkaListener(topics="delivery-events", groupId="notification-group")
    public void handleDelivery(String message) {
        String notification = "🚴 Delivery Notification: " + message;

        System.out.println(notification);

        // 🔥 PRODUCE to notification-events
        kafkaTemplate.send("notification-events", notification);
    }
}