package br.com.trevezani.cityinformation.config;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.trevezani.commons.config.DataConfigBase;

@Configuration
public class DataConfig extends DataConfigBase {
	Logger log = LoggerFactory.getLogger(this.getClass());

	@Bean(name = "connection")
    public Connection getConnection() {
		var classLoader = getClass().getClassLoader();
		var inputStream = classLoader.getResourceAsStream("data.zip");

		try {
			return super.getConnection(inputStream);
		} catch (Exception e) {
			log.error("Error: {}", e.getMessage());
		}

        return null;
	}
}
