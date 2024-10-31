package com.alkemy.bbva.polizaDeSeguros;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PolizaDeSegurosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolizaDeSegurosApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
		return args -> {
			System.out.println("Inicio de aplicación.");
		};
	}
}
