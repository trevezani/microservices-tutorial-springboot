package br.com.trevezani.tutorial.census.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude = ErrorMvcAutoConfiguration.class)
@ComponentScan("br.com.trevezani.tutorial")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
