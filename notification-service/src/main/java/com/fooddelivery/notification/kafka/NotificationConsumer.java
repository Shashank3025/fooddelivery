package com.fooddelivery.notification.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer 
{
	@KafkaListener(topics="payment-events",groupId="notification-group")
	public void handlepayment(String message)
	{
		System.out.println("\"💰 Payment Notification: \" + message");
		
	}
	
	@KafkaListener(topics="delivery-events",groupId="notification-group")
	public void handleDelivery(String message)
	{
		System.out.println("🚴 Delivery Notification: " + message);
	}

}
