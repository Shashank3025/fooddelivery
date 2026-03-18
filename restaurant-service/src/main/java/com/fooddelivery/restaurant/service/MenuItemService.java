package com.fooddelivery.restaurant.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.repository.MenuItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuItemService 
{
	private final MenuItemRepository menuItemRepository;
	
	public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

	public Page<MenuItem> getMenu(Long restaurantId, Pageable pageable) {
        return menuItemRepository.findByRestaurantId(restaurantId, pageable);
    }

}
