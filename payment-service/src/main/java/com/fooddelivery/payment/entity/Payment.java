package com.fooddelivery.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long id;
	Long orderId;
	Double amount;
	String status;
}
