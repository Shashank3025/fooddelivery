package com.fooddelivery.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> 
{
	

	User findByEmail(String email);


}
