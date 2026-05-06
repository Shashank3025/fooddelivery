package com.fooddelivery.payment.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.payment.entity.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer 
{
	private final KafkaTemplate<String,String> kafkaTemplate;
	
	public void sendPaymentSuccessEvent(Payment payment)
	{
		String message="paymentsuccess:"+payment.getOrderId();
		
		kafkaTemplate.send("payment-events",  String.valueOf(payment.getOrderId()),message);
		
		System.out.println("✅ Payment success event sent: " + message);
	}

}
