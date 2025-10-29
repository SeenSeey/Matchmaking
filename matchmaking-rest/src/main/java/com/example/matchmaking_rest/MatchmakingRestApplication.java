package com.example.matchmaking_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        scanBasePackages = {"com.example.matchmaking_rest", "com.example.matchmaking_api"},
        exclude = {DataSourceAutoConfiguration.class}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class MatchmakingRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchmakingRestApplication.class, args);
	}

}
