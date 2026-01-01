package com.phiny.labs.contentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ContentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentManagementApplication.class, args);
	}

}
