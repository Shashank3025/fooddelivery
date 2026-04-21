package com.fooddelivery.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    // REGISTER USER
    @PostMapping({"", "/"})
    public User createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);

        kafkaTemplate.send(
                TOPIC,
                String.valueOf(savedUser.getId()),
                "UserCreated:" + savedUser.getId()
        );

        return savedUser;
    }

    // LOGIN USER
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {

        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Incorrect password");
        }

        return ResponseEntity.ok(user);
    }

    // GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
