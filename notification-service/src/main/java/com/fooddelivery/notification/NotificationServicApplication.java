package com.fooddelivery.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class NotificationServicApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServicApplication.class, args);
	}

}
