package com.fooddelivery.order.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentEventConsumer 
{
	private final OrderRepository orderRepository;
	
	@KafkaListener(topics="payment-events",groupId="order-payment-group")
	public void consumePaymentEvent(String message) 
	{
	System.out.println("📩 Received payment event: " + message);

	if (!message.trim().toLowerCase().startsWith("paymentsuccess:")) {
	    return;
	}

    Long orderId = Long.parseLong(message.split(":")[1]);

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

    order.setStatus("CONFIRMED");
    orderRepository.save(order);
    System.out.println("✅ Order confirmed after payment: " + orderId);
    
	}
	
}

