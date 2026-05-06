package com.fooddelivery.cart.service;



import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fooddelivery.cart.dto.AddCartItemRequest;
import com.fooddelivery.cart.entity.Cart;
import com.fooddelivery.cart.entity.CartItem;

@Service
public class CartService {

    @Autowired
    private RedisTemplate<String, Cart> redisTemplate;

    private static final String CART_KEY_PREFIX = "cart:user:";

    public Cart addItemToCart(AddCartItemRequest request) {
        String key = CART_KEY_PREFIX + request.getUserId();

        Cart cart = redisTemplate.opsForValue().get(key);

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(request.getUserId());
            cart.setRestaurantId(request.getRestaurantId());
            cart.setItems(new ArrayList<>());
            cart.setTotalQuantity(0);
            cart.setTotalAmount(0.0);
        }

        CartItem existingItem = null;

        for (CartItem item : cart.getItems()) {
            if (
                Objects.equals(item.getMenuItemId(), request.getMenuItemId()) &&
                Objects.equals(item.getRestaurantId(), request.getRestaurantId())
            ) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setRestaurantId(request.getRestaurantId());
            newItem.setRestaurantName(request.getRestaurantName());
            newItem.setMenuItemId(request.getMenuItemId());
            newItem.setName(request.getName());
            newItem.setPrice(request.getPrice());
            newItem.setQuantity(request.getQuantity());

            cart.getItems().add(newItem);
        }

        recalculateCart(cart);
        redisTemplate.opsForValue().set(key, cart);

        return cart;
    }

    public Cart getCart(Long userId) {
        String key = CART_KEY_PREFIX + userId;

        Cart cart = redisTemplate.opsForValue().get(key);

        if (cart == null) {
            Cart emptyCart = new Cart();
            emptyCart.setUserId(userId);
            emptyCart.setItems(new ArrayList<>());
            emptyCart.setTotalQuantity(0);
            emptyCart.setTotalAmount(0.0);
            return emptyCart;
        }

        return cart;
    }

    public Cart deleteItem(Long userId, Long restaurantId, Long menuItemId) {
        String key = CART_KEY_PREFIX + userId;

        Cart cart = redisTemplate.opsForValue().get(key);

        if (cart == null) {
            return getCart(userId);
        }

        cart.getItems().removeIf(item ->
            Objects.equals(item.getRestaurantId(), restaurantId) &&
            Objects.equals(item.getMenuItemId(), menuItemId)
        );

        recalculateCart(cart);
        redisTemplate.opsForValue().set(key, cart);

        return cart;
    }

    private void recalculateCart(Cart cart) {
        int totalQuantity = 0;
        double totalAmount = 0.0;

        for (CartItem item : cart.getItems()) {
            totalQuantity += item.getQuantity();
            totalAmount += item.getPrice() * item.getQuantity();
        }

        cart.setTotalQuantity(totalQuantity);
        cart.setTotalAmount(totalAmount);
    }
}