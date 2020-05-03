package br.com.trevezani.commons.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

public abstract class RestTemplateBase {

	@Primary
	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
                .setConnectTimeout(Duration.ofMillis(30000))
                .setReadTimeout(Duration.ofMillis(30000))
                .build();
	}

}