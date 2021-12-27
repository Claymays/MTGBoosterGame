package com.mays.mtgboostergame;

import com.mays.mtgboostergame.API.RootController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MtgBoosterGameApplication {
	private static final Logger log =
			LoggerFactory.getLogger(MtgBoosterGameApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MtgBoosterGameApplication.class, args);
	}
}
