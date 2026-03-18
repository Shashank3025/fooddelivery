package com.fooddelivery.restaurant.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.restaurant.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> 
{
	Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);


}
