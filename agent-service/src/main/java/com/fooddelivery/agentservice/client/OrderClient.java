package com.fooddelivery.agentservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fooddelivery.agentservice.entity.CreateOrderRequestDto;
import com.fooddelivery.agentservice.entity.CreatedOrderResponseDto;

@Component
public class OrderClient 
{

    private final RestTemplate restTemplate;

    public OrderClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CreatedOrderResponseDto createOrder(CreateOrderRequestDto request) {
        String url = "http://order-service:8083/orders";
        return restTemplate.postForObject(url, request, CreatedOrderResponseDto.class);
    }
}