package com.team15gijo.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.team15gijo.post", "com.team15gijo.common"})
@EnableFeignClients
public class PostApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostApplication.class, args);
	}

}
