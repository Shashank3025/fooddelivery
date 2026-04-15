package com.fooddelivery.user.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import com.fooddelivery.user.model.User;
import com.fooddelivery.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    
    private static final String TOPIC = "user-events";

    @PostMapping({"", "/"})
    public User createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        // Publish Kafka event
        kafkaTemplate.send(
        	    "user-events",                     // topic name
        	    String.valueOf(user.getId()),      // key (unique per user)
        	    "UserCreated:" + user.getId()      // value (event payload)
        	);
        return savedUser;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}