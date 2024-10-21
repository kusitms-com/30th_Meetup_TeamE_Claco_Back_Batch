package com.curateme.clacobatchserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClacoBatchServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClacoBatchServerApplication.class, args);
	}

}
