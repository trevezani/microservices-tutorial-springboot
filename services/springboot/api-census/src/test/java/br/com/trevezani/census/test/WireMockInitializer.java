package br.com.trevezani.census.test;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import com.github.tomakehurst.wiremock.WireMockServer;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
		// TODO: new WireMockConfiguration().dynamicPort();
		WireMockServer wireMockServer = new WireMockServer(8080);
		wireMockServer.start();

		configurableApplicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);

		configurableApplicationContext.addApplicationListener(applicationEvent -> {
			if (applicationEvent instanceof ContextClosedEvent) {
				wireMockServer.stop();
			}
		});

		TestPropertyValues.of("zipcode_url:http://localhost:" + wireMockServer.port() + "/zipcode/37188").applyTo(configurableApplicationContext);
		TestPropertyValues.of("cityinformation_url:http://localhost:" + wireMockServer.port() + "/cityinformation/37188").applyTo(configurableApplicationContext);
	}
}
