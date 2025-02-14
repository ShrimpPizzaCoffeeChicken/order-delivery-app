package com.fortest.orderdelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class OrderdeliveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderdeliveryApplication.class, args);
	}

}
