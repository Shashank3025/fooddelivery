package com.fooddelivery.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fooddelivery.authentication.entity.User;
import com.fooddelivery.authentication.repository.UserRepository;

@Service
public class UserService 
{
	@Autowired
	UserRepository userRepository;
	
	public User saveUser(User user)
	{
		return userRepository.save(user);
	}
	
	public User findByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}
}
