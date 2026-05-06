package com.fooddelivery.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fooddelivery.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> 
{
	Payment findByOrderId(Long orderId);
	Optional<Payment> findByIdempotencyKey(String idempotencyKey);

}
