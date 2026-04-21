package com.fooddelivery.agentservice.entity;

import java.util.List;

public class CreateOrderRequestDto 
{
	private Long userId;
    private Long restaurantId;
    private List<OrderItemRequestDto> items;
    
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
	public List<OrderItemRequestDto> getItems() {
		return items;
	}
	public void setItems(List<OrderItemRequestDto> items) {
		this.items = items;
	}
	
	
	
	public CreateOrderRequestDto() {
    }

    public CreateOrderRequestDto(Long userId, Long restaurantId, List<OrderItemRequestDto> items) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
    }


}
