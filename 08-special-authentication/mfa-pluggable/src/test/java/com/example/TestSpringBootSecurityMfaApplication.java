package com.example;

import org.springframework.boot.SpringApplication;

public class TestSpringBootSecurityMfaApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringBootSecurityMfaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
