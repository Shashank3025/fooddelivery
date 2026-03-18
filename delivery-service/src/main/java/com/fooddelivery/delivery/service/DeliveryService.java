package com.fooddelivery.delivery.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.delivery.entity.Delivery;
import com.fooddelivery.delivery.repository.DeliveryRepository;

@Service
public class DeliveryService 
{
	DeliveryRepository deliveryRepository;
	KafkaTemplate<String,String> kafkaTemplate;
	
	public DeliveryService(DeliveryRepository deliveryRepository, KafkaTemplate<String, String> kafkaTemplate) 
	{
        this.deliveryRepository = deliveryRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
	
	public Delivery assignDelivery(Long orderId)
	{
		Delivery delivery=new Delivery();
		delivery.setOrderId(orderId);
		delivery.setStatus("ASSIGNED");
		deliveryRepository.save(delivery);
		
		kafkaTemplate.send("delivery-events",String.valueOf(orderId),"DeliveryAssigned:"+orderId);
		return delivery;
	}


}
