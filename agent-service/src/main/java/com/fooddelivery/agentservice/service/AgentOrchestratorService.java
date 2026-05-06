package com.fooddelivery.agentservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            String message = normalize(request.getMessage());

            if (isSearchQuestion(message)) {
                String keyword = extractFoodKeyword(message);
                List<MenuOption> options = searchMenuOptions(keyword);

                if (options.isEmpty()) {
                    return response("SUCCESS",
                            "Sorry, I couldn’t find any " + keyword + " options.",
                            request);
                }

                String reply = buildOptionsReply(keyword, options);

                return response("SUCCESS", reply, request);
            }

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

            return response("SUCCESS",
                    "Sorry, I couldn’t find a matching item. Try asking: what dosas are available?",
                    request);

        } catch (Exception e) {
            return response("FAILED",
                    "Agent could not process request: " + e.getMessage(),
                    request);
        }
    }

    private boolean isSearchQuestion(String message) {
        return message.contains("available")
                || message.contains("show")
                || message.contains("what")
                || message.contains("options")
                || message.startsWith("want ")
                || message.contains("do we have");
    }

    private String extractFoodKeyword(String message) {
        return message
                .replaceAll("\\b(what|which|show|available|options|are|is|there|do|we|have|want|please|me|any|other|types|of)\\b", "")
                .replaceAll("\\s+", " ")
                .trim()
                .replace("dosas", "dosa")
                .replace("biryanis", "biryani");
    }

    private List<MenuOption> searchMenuOptions(String keyword) {
        List<MenuOption> results = new ArrayList<>();

        if (keyword == null || keyword.isBlank()) {
            return results;
        }

        List<RestaurantDto> restaurants = restaurantClient.getAllRestaurants();

        for (RestaurantDto restaurant : restaurants) {
            try {
                List<MenuItemDto> menuItems = menuClient.getMenuByRestaurantId(restaurant.getId());

                if (menuItems == null) continue;

                for (MenuItemDto item : menuItems) {
                    if (item.getName() == null) continue;

                    String itemName = normalize(item.getName());

                    if (itemName.contains(keyword)) {
                        results.add(new MenuOption(restaurant, item));
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return results;
    }

    private String buildOptionsReply(String keyword, List<MenuOption> options) {
        String items = options.stream()
                .map(option -> "🍽️ " + option.menuItem.getName()
                        + " - $" + option.menuItem.getPrice()
                        + " from " + option.restaurant.getName())
                .collect(Collectors.joining("\n"));

        return "Here are the " + keyword + " options available:\n\n"
                + items
                + "\n\nTo order, say: order " + options.get(0).menuItem.getName();
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

            } catch (Exception ignored) {
            }
        }

        return bestScore >= 70 ? bestMatch : null;
    }

    private int calculateMenuMatchScore(String message, String menuItemName) {
        if (message.equals(menuItemName)) return 100;
        if (message.contains(menuItemName)) return 95;
        if (menuItemName.contains(message)) return 85;
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

        if (itemWords.length == 0) return 0;

        return (matchedWords * 100) / itemWords.length;
    }

    private String normalize(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String removeCommandWords(String text) {
        return text
                .replaceAll("\\b(order|from|get|me|please|restaurant)\\b", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private AgentOrderResponse response(String status, String message, AgentOrderRequest request) {
        return new AgentOrderResponse(
                status,
                message,
                request.getUserId(),
                request.getMessage(),
                null,
                null,
                null,
                null
        );
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

    private static class MenuOption {
        private final RestaurantDto restaurant;
        private final MenuItemDto menuItem;

        public MenuOption(RestaurantDto restaurant, MenuItemDto menuItem) {
            this.restaurant = restaurant;
            this.menuItem = menuItem;
        }
    }
}