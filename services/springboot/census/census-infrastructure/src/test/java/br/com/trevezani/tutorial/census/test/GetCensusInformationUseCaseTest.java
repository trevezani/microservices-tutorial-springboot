package br.com.trevezani.tutorial.census.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import br.com.trevezani.tutorial.census.core.Census;
import br.com.trevezani.tutorial.census.core.exeption.ValidationException;
import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCase;
import br.com.trevezani.tutorial.census.core.usecase.GetCensusInformationUseCaseImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusDemographyRestServiceImpl;
import br.com.trevezani.tutorial.census.infrastructure.rest.impl.CensusZipCodeRestServiceImpl;
import br.com.trevezani.tutorial.internal.communication.HTTPCommunication;
import br.com.trevezani.tutorial.internal.communication.exception.BusinessException;
import br.com.trevezani.tutorial.internal.communication.exception.InternalErrorException;
import br.com.trevezani.tutorial.internal.delivery.rest.DemographyRest;
import br.com.trevezani.tutorial.internal.delivery.rest.ZipCodeRest;

@TestPropertySource(properties = {
		"local.server.port=8081"
})
@ContextConfiguration(initializers = { WireMockInitializer.class })
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
class GetCensusInformationUseCaseTest {
	@Autowired
	private WireMockServer wireMockServer;

	@LocalServerPort
	private Integer port;

	private GetCensusInformationUseCase getCensusInformationUseCase;
	
	@BeforeEach
	public void setUp() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		
		this.wireMockServer.stubFor(WireMock.get("/zipcode/37188")
				.willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBodyFile("json/zipcode_37188.json")));

		this.wireMockServer.stubFor(WireMock.get("/demography/37188")
				.willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBodyFile("json/demography_37188.json")));

		getCensusInformationUseCase = new GetCensusInformationUseCaseImpl(
				new CensusDemographyRestServiceImpl(
						new HTTPCommunication<DemographyRest>(DemographyRest.class, restTemplate), 
						"http://localhost:8080"), 
				new CensusZipCodeRestServiceImpl(
						new HTTPCommunication<ZipCodeRest>(ZipCodeRest.class, restTemplate), 
						"http://localhost:8080"));
	}
	
	@AfterEach
	public void afterEach() {
		this.wireMockServer.resetAll();
	}

	@DisplayName("Test Integration :: Get census information")
	@Test
	void test1() throws ValidationException, BusinessException, InternalErrorException {
		Census census = getCensusInformationUseCase.execute("xxx", "37188");

		assertNotNull(census);

		assertEquals("White House", census.getPrimaryCity());		
		assertEquals("Tennessee", census.getStateName());		
	}

	@DisplayName("Test Integration :: 404 Error")
	@Test
	void test2() throws ValidationException, BusinessException {
		assertThrows(InternalErrorException.class, () -> {
			getCensusInformationUseCase.execute("xxx", "37187");
		});		
	}
}
