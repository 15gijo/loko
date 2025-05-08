package com.team15gijo.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.team15gijo.search", "com.team15gijo.common"})
@EnableFeignClients
@EnableScheduling
public class SearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchApplication.class, args);
	}

}
