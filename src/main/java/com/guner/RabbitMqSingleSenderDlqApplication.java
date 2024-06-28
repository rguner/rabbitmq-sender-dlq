package com.guner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitMqSingleSenderDlqApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqSingleSenderDlqApplication.class, args);
	}

}
