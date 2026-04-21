
package com.fooddelivery.agentservice.entity;

import java.util.List;

public class AiAgentResponse {

    private String intent;
    private List<String> items;
    private List<String> preferences;
    private int quantity;
    private String reply;
    private boolean awaitingConfirmation;
    private RestaurantDto matchedRestaurant;
    private MenuItemDto matchedMenuItem;

    public AiAgentResponse() {
    }

    public AiAgentResponse(String intent, List<String> items, List<String> preferences,
                           int quantity, String reply, boolean awaitingConfirmation,
                           RestaurantDto matchedRestaurant, MenuItemDto matchedMenuItem) {
        this.intent = intent;
        this.items = items;
        this.preferences = preferences;
        this.quantity = quantity;
        this.reply = reply;
        this.awaitingConfirmation = awaitingConfirmation;
        this.matchedRestaurant = matchedRestaurant;
        this.matchedMenuItem = matchedMenuItem;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public boolean isAwaitingConfirmation() {
        return awaitingConfirmation;
    }

    public void setAwaitingConfirmation(boolean awaitingConfirmation) {
        this.awaitingConfirmation = awaitingConfirmation;
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
}