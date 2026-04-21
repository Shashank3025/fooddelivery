package com.fooddelivery.agentservice.entity;

public class CreatedOrderItemDto 
{
	private Long id;
    private Long menuItemId;
    private int quantity;

    public CreatedOrderItemDto() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
