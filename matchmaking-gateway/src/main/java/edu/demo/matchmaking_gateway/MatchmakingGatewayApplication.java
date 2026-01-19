package edu.demo.matchmaking_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        scanBasePackages = {"edu.demo.matchmaking_gateway", "com.example.matchmaking_api"},
        exclude = {DataSourceAutoConfiguration.class}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class MatchmakingGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchmakingGatewayApplication.class, args);
	}

}
