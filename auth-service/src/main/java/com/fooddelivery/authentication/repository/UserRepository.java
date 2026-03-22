package com.fooddelivery.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.authentication.entity.User;

public interface UserRepository extends JpaRepository<User, Long> 
{

	User findByUsername(String username);
	

}
