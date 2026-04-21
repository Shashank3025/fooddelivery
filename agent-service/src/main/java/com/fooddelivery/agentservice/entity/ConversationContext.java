package com.fooddelivery.agentservice.entity;

import java.util.List;

public class ConversationContext {

    private String lastIntent;
    private List<String> items;
    private List<String> preferences;
    private int quantity;
    private boolean awaitingConfirmation;

    private RestaurantDto suggestedRestaurant;
    private MenuItemDto suggestedMenuItem;

    public ConversationContext() {
    }

    public ConversationContext(String lastIntent, List<String> items, List<String> preferences,
                               int quantity, boolean awaitingConfirmation) {
        this.lastIntent = lastIntent;
        this.items = items;
        this.preferences = preferences;
        this.quantity = quantity;
        this.awaitingConfirmation = awaitingConfirmation;
    }

    public String getLastIntent() {
        return lastIntent;
    }

    public void setLastIntent(String lastIntent) {
        this.lastIntent = lastIntent;
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

    public boolean isAwaitingConfirmation() {
        return awaitingConfirmation;
    }

    public void setAwaitingConfirmation(boolean awaitingConfirmation) {
        this.awaitingConfirmation = awaitingConfirmation;
    }

    public RestaurantDto getSuggestedRestaurant() {
        return suggestedRestaurant;
    }

    public void setSuggestedRestaurant(RestaurantDto suggestedRestaurant) {
        this.suggestedRestaurant = suggestedRestaurant;
    }

    public MenuItemDto getSuggestedMenuItem() {
        return suggestedMenuItem;
    }

    public void setSuggestedMenuItem(MenuItemDto suggestedMenuItem) {
        this.suggestedMenuItem = suggestedMenuItem;
    }
}