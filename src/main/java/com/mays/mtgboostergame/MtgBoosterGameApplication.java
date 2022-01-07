package com.mays.mtgboostergame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "com"})
public class MtgBoosterGameApplication {
	private static final Logger log =
			LoggerFactory.getLogger(MtgBoosterGameApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MtgBoosterGameApplication.class, args);
	}
}
