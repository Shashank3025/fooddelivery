package com.fooddelivery.agentservice.client;


import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fooddelivery.agentservice.entity.MenuItemDto;
import com.fooddelivery.agentservice.entity.MenuPageResponse;

@Component
public class MenuClient {

    private final RestTemplate restTemplate;

    public MenuClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<MenuItemDto> getMenuByRestaurantId(Long restaurantId) {
        String url = "http://restaurant-service:8082/restaurants/" + restaurantId + "/menu";

        MenuPageResponse response = restTemplate.getForObject(url, MenuPageResponse.class);

        if (response != null && response.getContent() != null) {
            return response.getContent();
        }

        return Collections.emptyList();
    }
}