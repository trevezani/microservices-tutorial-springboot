package br.com.trevezani.tutorial.zipcode.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("br.com.trevezani.tutorial.zipcode")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
