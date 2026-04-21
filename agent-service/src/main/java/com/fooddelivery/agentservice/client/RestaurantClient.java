package com.fooddelivery.agentservice.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fooddelivery.agentservice.entity.RestaurantDto;

@Component
public class RestaurantClient 
{
	private final RestTemplate restTemplate;
	
	public RestaurantClient(RestTemplate restTemplate) 
	{
        this.restTemplate = restTemplate;
    }

    public List<RestaurantDto> getAllRestaurants() 
    {
        String url = "http://restaurant-service:8082/restaurants";

        return restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<RestaurantDto>>() {}
        ).getBody();
    }

}
