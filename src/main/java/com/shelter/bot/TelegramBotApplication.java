package com.shelter.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}
}
