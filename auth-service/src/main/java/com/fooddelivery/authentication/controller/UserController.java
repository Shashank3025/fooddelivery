package com.fooddelivery.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.authentication.entity.User;
import com.fooddelivery.authentication.service.UserService;
import com.fooddelivery.authentication.token.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController 
{
	private final UserService userService;
	private final JwtUtil jwtUtil;
	
	@PostMapping("/saveuser")
	public User saveuser(@RequestBody User user)
	{
		User saveuser=userService.saveUser(user);
		return saveuser;
	}
	
	@PostMapping("/login")
	public String loginuser(@RequestBody User user)
	{
	    User finduser = userService.findByUsername(user.getUsername());

	    if (finduser == null || 
	        !finduser.getPassword().equals(user.getPassword())) {
	        throw new RuntimeException("Invalid credentials");
	    }

	    return jwtUtil.generateToken(user.getUsername());
	}
	
	

}
