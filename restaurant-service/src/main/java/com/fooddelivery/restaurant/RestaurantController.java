package com.fooddelivery.restaurant;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.service.MenuItemService;
import com.fooddelivery.restaurant.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController 
{
	private final RestaurantService restaurantService;
	
	private final MenuItemService menuItemService;
	
	private final KafkaTemplate<String,String> kafkaTemplate;
	
	@PostMapping
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) 
	{
		Restaurant savedRestaurant=restaurantService.saveRestaurant(restaurant);
		try {
	        kafkaTemplate.send(
	            "restaurant-event",
	            "restaurant added: " + savedRestaurant.getName() + " id " + savedRestaurant.getId()
	        );
	        System.out.println("Kafka event sent for restaurant");
	    } catch (Exception e) {
	        System.out.println("Kafka send failed: " + e.getMessage());
	    }
		return savedRestaurant;
    }
	
	@GetMapping("/name/{name}")
	public Restaurant getRestaurantByName(@PathVariable String name)
	{
		kafkaTemplate.send("restaurant-event","finding restaurant by name:"+name);
		return restaurantService.getRestaurantByName(name);
	}

    @GetMapping
    public List<Restaurant> getAllRestaurants() 
    {
        return restaurantService.getAllRestaurants();
    }

    @PostMapping("/{id}/menu")
    public MenuItem addMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItem) 
    {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        menuItem.setRestaurant(restaurant);

        MenuItem savedMenu = menuItemService.addMenuItem(menuItem);

        try {
            kafkaTemplate.send(
                "restaurant-event",
                "menu item added: " + savedMenu.getName() + " for restaurant " + id
            );
            System.out.println("Kafka event sent for menu");
        } catch (Exception e) {
            System.out.println("Kafka send failed: " + e.getMessage());
        }

        return savedMenu;
    }


    @GetMapping("/{id}/menu")
    public Page<MenuItem> getMenu(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return menuItemService.getMenu(id, pageable);
    }


	

}
