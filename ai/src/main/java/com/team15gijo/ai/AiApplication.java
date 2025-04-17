package com.team15gijo.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.team15gijo.comment", "com.team15gijo.common"})
@EnableFeignClients
public class CommentApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommentApplication.class, args);
	}

}
