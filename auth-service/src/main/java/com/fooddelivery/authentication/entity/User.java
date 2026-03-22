package com.fooddelivery.authentication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@Table(name="userauth")
public class User 
{
	@Id
	@GeneratedValue
	Long id;
	String username;
	String password;
	String role;
	

}
