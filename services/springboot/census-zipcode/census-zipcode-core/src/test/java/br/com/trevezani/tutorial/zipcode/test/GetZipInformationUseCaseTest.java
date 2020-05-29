package br.com.trevezani.tutorial.zipcode.test;

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

import br.com.trevezani.tutorial.zipcode.core.ZipCode;
import br.com.trevezani.tutorial.zipcode.core.exception.InformationNotExistException;
import br.com.trevezani.tutorial.zipcode.core.exception.ValidationException;
import br.com.trevezani.tutorial.zipcode.core.port.ZipCodeRepositoryService;
import br.com.trevezani.tutorial.zipcode.core.usecase.GetZipInformationUseCase;
import br.com.trevezani.tutorial.zipcode.core.usecase.GetZipInformationUseCaseImpl;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class GetZipInformationUseCaseTest {
	@Mock
	private ZipCodeRepositoryService zipCodeRepositoryService;
	
	private GetZipInformationUseCase getZipInformationUseCase;
	
	@BeforeEach
	void setUp() throws Exception {
		getZipInformationUseCase = new GetZipInformationUseCaseImpl(zipCodeRepositoryService);
		
		Optional<ZipCode> item = Optional.of(new ZipCode("Test", "Test", "Test", "Test", "Test"));
		
		Mockito.lenient().when(zipCodeRepositoryService.findByZip("111111")).thenReturn(item);
	}	
	
	@DisplayName("Test Mock :: Valid zip")
	@Test
	void test1() throws InformationNotExistException, ValidationException, SQLException {
		assertNotNull(getZipInformationUseCase.execute("111111").getState());
		assertEquals("Test", getZipInformationUseCase.execute("111111").getState());
	}

	@DisplayName("Test Mock :: Blank value")
	@Test
	void test2() {
		assertThrows(ValidationException.class, () -> {
			getZipInformationUseCase.execute("");
		});
	}

	@DisplayName("Test Mock :: Invalid value")
	@Test
	void test3() {
		assertThrows(InformationNotExistException.class, () -> {
			getZipInformationUseCase.execute("111112");
		});
	}	
}
