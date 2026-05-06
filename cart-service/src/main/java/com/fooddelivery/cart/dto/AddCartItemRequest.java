package com.fooddelivery.cart.dto;

public class AddCartItemRequest 
{
	  private Long userId;
	  private Long restaurantId;
	  private Long menuItemId;
	  private String name;
	  private Double price;
	  private String restaurantName;

	  public String getRestaurantName() {
	      return restaurantName;
	  }

	  public void setRestaurantName(String restaurantName) {
	      this.restaurantName = restaurantName;
	  }
	  private Integer quantity;
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
	  public Long getMenuItemId() {
		  return menuItemId;
	  }
	  public void setMenuItemId(Long menuItemId) {
		  this.menuItemId = menuItemId;
	  }
	  public String getName() {
		  return name;
	  }
	  public void setName(String name) {
		  this.name = name;
	  }
	  public Double getPrice() {
		  return price;
	  }
	  public void setPrice(Double price) {
		  this.price = price;
	  }
	  public Integer getQuantity() {
		  return quantity;
	  }
	  public void setQuantity(Integer quantity) {
		  this.quantity = quantity;
	  }


}
