package com.fooddelivery.restaurant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService 
{
	private final RestaurantRepository restaurantRepository;
	
	public Restaurant saveRestaurant(Restaurant restaurant)
	{
		return restaurantRepository.save(restaurant);
	}
	
	public Restaurant getRestaurantByName(String name) {
	    return restaurantRepository.findByName(name)
	            .orElseThrow(() -> new RuntimeException("Restaurant not found: " + name));
	}

	
	public List<Restaurant> getAllRestaurants()
	{
		return restaurantRepository.findAll();
	}
	
	public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }


}
