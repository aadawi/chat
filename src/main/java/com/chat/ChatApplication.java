package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatApplication {
	public static void main(String[] args) {
		System.out.println("START APP");
		SpringApplication.run(ChatApplication.class, args);
	}
}
