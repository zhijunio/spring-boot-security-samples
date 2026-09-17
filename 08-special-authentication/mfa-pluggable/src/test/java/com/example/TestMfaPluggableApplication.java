package com.example;

import org.springframework.boot.SpringApplication;

public class TestMfaPluggableApplication {

	public static void main(String[] args) {
		SpringApplication.from(MfaPluggableApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
