package br.com.trevezani.census.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
		"zipcode.api.url=http://localhost:8080", 
		"cityinformation.api.url=http://localhost:8080"})
@ContextConfiguration(initializers = { WireMockInitializer.class })
@ActiveProfiles("test")
class CensusIntegrationTest {
	@Autowired
	private WireMockServer wireMockServer;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private Integer port;

	@AfterEach
	public void afterEach() {
		this.wireMockServer.resetAll();
	}

	@DisplayName("Test Integration :: Get census information")
	@Test
	void test1() {
		this.wireMockServer.stubFor(WireMock.get("/zipcode/37188")
				.willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBodyFile("json/zipcode_37188.json")));

		this.wireMockServer.stubFor(WireMock.get("/cityinformation/37188")
				.willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBodyFile("json/cityinformation_37188.json")));
		
		var responseType = new ParameterizedTypeReference<Map<String, String>>() {};

		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		var entity = new HttpEntity<String>("parameters", headers);		
		
		var url = "http://localhost:" + port + "/info/zip/37188";
		var result = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
		
		var body = result.getBody();
		
		assertFalse(body.isEmpty(), "Empty return");
		assertTrue(body.size() == 8, "Invalid elements size");

		assertNotNull(body.get("primary_city"));
		assertEquals("White House", body.get("primary_city"));		
		
		assertNotNull(body.get("state_name"));
		assertEquals("Tennessee", body.get("state_name"));		
	}
}
