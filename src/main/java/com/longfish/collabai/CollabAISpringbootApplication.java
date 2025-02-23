package com.longfish.collabai;

import okhttp3.OkHttpClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@MapperScan("com.longfish.collabai.mapper")
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
public class CollabAISpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollabAISpringbootApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient().newBuilder().build();
	}

}
