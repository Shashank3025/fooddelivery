package com.fooddelivery.payment.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fooddelivery.payment.dto.PaymentRequest;
import com.fooddelivery.payment.dto.PaymentResponse;
import com.fooddelivery.payment.entity.Payment;
import com.fooddelivery.payment.kafka.PaymentEventProducer;
import com.fooddelivery.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService 
{
	private final PaymentRepository paymentRepository;
	
	private final PaymentEventProducer paymentEventProducer;
	
	public PaymentResponse processPayment(PaymentRequest request) {

	    if (request.getIdempotencyKey() != null) {
	        Optional<Payment> existingPayment =
	                paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());

	        if (existingPayment.isPresent()) {
	            Payment existing = existingPayment.get();

	            return new PaymentResponse(
	                    existing.getId(),
	                    existing.getOrderId(),
	                    existing.getAmount(),
	                    existing.getStatus(),
	                    existing.getPaymentMethod(),
	                    existing.getTransactionId(),
	                    "Duplicate request detected. Returning existing payment."
	            );
	        }
	    }

	    Payment payment = new Payment();
	    payment.setAmount(request.getAmount());
	    payment.setCreatedAt(LocalDateTime.now());
	    payment.setOrderId(request.getOrderId());
	    payment.setPaymentMethod(request.getPaymentMethod());
	    payment.setStatus("INITIATED");
	    payment.setTransactionId("TXN-" + UUID.randomUUID());
	    payment.setUpdatedAt(LocalDateTime.now());
	    payment.setIdempotencyKey(request.getIdempotencyKey());

	    Payment savedPayment = paymentRepository.save(payment);

	    savedPayment.setStatus("PROCESSING");
	    savedPayment.setUpdatedAt(LocalDateTime.now());
	    savedPayment = paymentRepository.save(savedPayment);

	    savedPayment.setStatus("PAID");
	    savedPayment.setUpdatedAt(LocalDateTime.now());
	    savedPayment = paymentRepository.save(savedPayment);

	    paymentEventProducer.sendPaymentSuccessEvent(savedPayment);

	    return new PaymentResponse(
	            savedPayment.getId(),
	            savedPayment.getOrderId(),
	            savedPayment.getAmount(),
	            savedPayment.getStatus(),
	            savedPayment.getPaymentMethod(),
	            savedPayment.getTransactionId(),
	            "Payment Completed Successfully"
	    );
	}
	public Payment getPaymentByOrderId(Long orderId)
	{
		return paymentRepository.findByOrderId(orderId);
	}

}
