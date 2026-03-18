package com.fooddelivery.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	Long orderId;
	String deliveryPerson;
	String status; //assigned, picked, delivered

}
