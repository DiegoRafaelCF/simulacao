package com.hack.simulacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SimulacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimulacaoApplication.class, args);
	}

}
