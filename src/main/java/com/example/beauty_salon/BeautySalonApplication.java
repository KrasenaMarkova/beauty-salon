package com.example.beauty_salon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class BeautySalonApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeautySalonApplication.class, args);
	}

}
