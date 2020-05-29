package br.com.trevezani.tutorial.demography.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.trevezani.tutorial.demography.core.Demography;
import br.com.trevezani.tutorial.demography.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.demography.core.exception.ValidationException;
import br.com.trevezani.tutorial.demography.core.port.DemographyRepositoryService;
import br.com.trevezani.tutorial.demography.core.usecase.GetDemographyInformationUseCase;
import br.com.trevezani.tutorial.demography.core.usecase.GetDemographyInformationUseCaseImpl;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class GetDemographyInformationUseCaseTest {
	@Mock
	private DemographyRepositoryService demographyRepositoryService;
	
	private GetDemographyInformationUseCase getDemographyInformationUseCase;
	
	@BeforeEach
	void setUp() throws Exception {
		getDemographyInformationUseCase = new GetDemographyInformationUseCaseImpl(demographyRepositoryService);
		
		Optional<Demography> item = Optional.of(new Demography("Test", "Test", "Test"));
		
		Mockito.lenient().when(demographyRepositoryService.findByZip("111111")).thenReturn(item);
	}	
	
	@DisplayName("Test Mock :: Valid zip")
	@Test
	void test1() throws InformationNotExistException, ValidationException, SQLException {
		assertNotNull(getDemographyInformationUseCase.execute("111111").getStateName());
		assertEquals("Test", getDemographyInformationUseCase.execute("111111").getStateName());
	}

	@DisplayName("Test Mock :: Blank value")
	@Test
	void test2() {
		assertThrows(ValidationException.class, () -> {
			getDemographyInformationUseCase.execute("");
		});
	}

	@DisplayName("Test Mock :: Invalid value")
	@Test
	void test3() {
		assertThrows(InformationNotExistException.class, () -> {
			getDemographyInformationUseCase.execute("111112");
		});
	}	
}
