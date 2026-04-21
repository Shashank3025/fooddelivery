package com.fooddelivery.agentservice.entity;

public class OrderItemRequestDto 

{
	private Long menuItemId;
    private int quantity;

    public OrderItemRequestDto() {
    }

    public OrderItemRequestDto(Long menuItemId, int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

	public Long getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Long menuItemId) {
		this.menuItemId = menuItemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
    

}
