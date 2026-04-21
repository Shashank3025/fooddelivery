package com.fooddelivery.agentservice.entity;



import java.util.List;

public class AgentOrderResponse {

    private String status;
    private String message;
    private Long userId;
    private String userInput;
    private RestaurantDto matchedRestaurant;
    private MenuItemDto matchedMenuItem;
    private CreatedOrderResponseDto createdOrder;
    private List<RestaurantDto> restaurants;

    public AgentOrderResponse() {
    }

    public AgentOrderResponse(String status, String message, Long userId, String userInput,
                              RestaurantDto matchedRestaurant, MenuItemDto matchedMenuItem,
                              CreatedOrderResponseDto createdOrder,
                              List<RestaurantDto> restaurants) {
        this.status = status;
        this.message = message;
        this.userId = userId;
        this.userInput = userInput;
        this.matchedRestaurant = matchedRestaurant;
        this.matchedMenuItem = matchedMenuItem;
        this.createdOrder = createdOrder;
        this.restaurants = restaurants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public RestaurantDto getMatchedRestaurant() {
        return matchedRestaurant;
    }

    public void setMatchedRestaurant(RestaurantDto matchedRestaurant) {
        this.matchedRestaurant = matchedRestaurant;
    }

    public MenuItemDto getMatchedMenuItem() {
        return matchedMenuItem;
    }

    public void setMatchedMenuItem(MenuItemDto matchedMenuItem) {
        this.matchedMenuItem = matchedMenuItem;
    }

    public CreatedOrderResponseDto getCreatedOrder() {
        return createdOrder;
    }

    public void setCreatedOrder(CreatedOrderResponseDto createdOrder) {
        this.createdOrder = createdOrder;
    }

    public List<RestaurantDto> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantDto> restaurants) {
        this.restaurants = restaurants;
    }
}