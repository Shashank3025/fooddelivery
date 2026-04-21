package com.fooddelivery.agentservice.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.fooddelivery.agentservice.client.MenuClient;
import com.fooddelivery.agentservice.client.OrderClient;
import com.fooddelivery.agentservice.client.RestaurantClient;
import com.fooddelivery.agentservice.entity.AgentOrderRequest;
import com.fooddelivery.agentservice.entity.AgentOrderResponse;
import com.fooddelivery.agentservice.entity.AgentResponse;
import com.fooddelivery.agentservice.entity.CreateOrderRequestDto;
import com.fooddelivery.agentservice.entity.CreatedOrderResponseDto;
import com.fooddelivery.agentservice.entity.MenuItemDto;
import com.fooddelivery.agentservice.entity.OrderItemRequestDto;
import com.fooddelivery.agentservice.entity.RestaurantDto;

@Service
public class AgentOrchestratorService {

    private final RestaurantClient restaurantClient;
    private final MenuClient menuClient;
    private final OrderClient orderClient;

    public AgentOrchestratorService(RestaurantClient restaurantClient,
                                    MenuClient menuClient,
                                    OrderClient orderClient) {
        this.restaurantClient = restaurantClient;
        this.menuClient = menuClient;
        this.orderClient = orderClient;
    }

    public AgentResponse healthCheck() {
        return new AgentResponse("Agent service is running", "SUCCESS");
    }

    public AgentOrderResponse placeOrder(AgentOrderRequest request) {
        try {
            List<RestaurantDto> restaurants = restaurantClient.getAllRestaurants();
            MatchResult bestMatch = findBestMenuMatch(request.getMessage(), restaurants);

            if (bestMatch != null) {
                CreateOrderRequestDto orderRequest = new CreateOrderRequestDto(
                        request.getUserId(),
                        bestMatch.getRestaurant().getId(),
                        List.of(new OrderItemRequestDto(bestMatch.getMenuItem().getId(), 1))
                );

                CreatedOrderResponseDto createdOrder = orderClient.createOrder(orderRequest);

                return new AgentOrderResponse(
                        "SUCCESS",
                        "Order placed successfully by agent",
                        request.getUserId(),
                        request.getMessage(),
                        bestMatch.getRestaurant(),
                        bestMatch.getMenuItem(),
                        createdOrder,
                        null
                );
            }

            return new AgentOrderResponse(
                    "SUCCESS",
                    "Sorry, I couldn’t find a matching item. Please try something else.",
                    request.getUserId(),
                    request.getMessage(),
                    null,
                    null,
                    null,
                    null
            );

        } catch (Exception e) {
            return new AgentOrderResponse(
                    "FAILED",
                    "Agent could not process order request: " + e.getMessage(),
                    request.getUserId(),
                    request.getMessage(),
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    private MatchResult findBestMenuMatch(String userMessage, List<RestaurantDto> restaurants) {
        if (userMessage == null || restaurants == null || restaurants.isEmpty()) {
            return null;
        }

        String cleanedMessage = removeCommandWords(normalize(userMessage));
        MatchResult bestMatch = null;
        int bestScore = 0;

        for (RestaurantDto restaurant : restaurants) {
            try {
                List<MenuItemDto> menuItems = menuClient.getMenuByRestaurantId(restaurant.getId());

                if (menuItems == null || menuItems.isEmpty()) {
                    continue;
                }

                for (MenuItemDto menuItem : menuItems) {
                    if (menuItem.getName() == null || menuItem.getName().isBlank()) {
                        continue;
                    }

                    String normalizedMenuItemName = normalize(menuItem.getName());
                    int score = calculateMenuMatchScore(cleanedMessage, normalizedMenuItemName);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMatch = new MatchResult(restaurant, menuItem);
                    }
                }

            } catch (Exception e) {
                // skip restaurant if menu fetch fails
            }
        }

        return bestScore >= 70 ? bestMatch : null;
    }

    private int calculateMenuMatchScore(String message, String menuItemName) {
        if (message.equals(menuItemName)) {
            return 100;
        }

        if (message.contains(menuItemName)) {
            return 95;
        }

        if (menuItemName.contains(message) && message.split("\\s+").length > 1) {
            return 85;
        }

        return wordOverlapScore(message, menuItemName);
    }

    private int wordOverlapScore(String message, String itemName) {
        String[] messageWords = message.split("\\s+");
        String[] itemWords = itemName.split("\\s+");

        int matchedWords = 0;

        for (String itemWord : itemWords) {
            for (String messageWord : messageWords) {
                if (itemWord.equals(messageWord)) {
                    matchedWords++;
                    break;
                }
            }
        }

        if (itemWords.length == 0) {
            return 0;
        }

        return (matchedWords * 100) / itemWords.length;
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String removeCommandWords(String text) {
        return text
                .replaceAll("\\b(order|from|want|get|me|please|restaurant)\\b", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static class MatchResult {
        private final RestaurantDto restaurant;
        private final MenuItemDto menuItem;

        public MatchResult(RestaurantDto restaurant, MenuItemDto menuItem) {
            this.restaurant = restaurant;
            this.menuItem = menuItem;
        }

        public RestaurantDto getRestaurant() {
            return restaurant;
        }

        public MenuItemDto getMenuItem() {
            return menuItem;
        }
    }
}