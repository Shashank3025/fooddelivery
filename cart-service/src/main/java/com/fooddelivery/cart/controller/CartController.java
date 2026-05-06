package com.fooddelivery.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.cart.dto.AddCartItemRequest;
import com.fooddelivery.cart.entity.Cart;
import com.fooddelivery.cart.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController 
{
	@Autowired
	CartService cartService;
	
	@PostMapping("/items")
	public Cart addItemToCart(@RequestBody AddCartItemRequest request)
	{
		return cartService.addItemToCart(request);
	}
	
	@GetMapping("/viewcart/{userId}")
	public Cart showCart(@PathVariable Long userId) {
	    return cartService.getCart(userId);
	}
	@GetMapping("/test")
	public String test() {
	    return "cart service working";
	}
	
	@DeleteMapping("/items/{userId}/{restaurantId}/{menuItemId}")
	public Cart deleteItem(
	    @PathVariable Long userId,
	    @PathVariable Long restaurantId,
	    @PathVariable Long menuItemId
	) {
	    return cartService.deleteItem(userId, restaurantId, menuItemId);
	}
}
