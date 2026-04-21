package com.fooddelivery.agentservice.entity;

import java.util.List;

public class CreatedOrderResponseDto 
{
	private Long id;
    private Long userId;
    private Long restaurantId;
    private String status;
    private List<CreatedOrderItemDto> items;

    public CreatedOrderResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CreatedOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<CreatedOrderItemDto> items) {
        this.items = items;
    }

}
