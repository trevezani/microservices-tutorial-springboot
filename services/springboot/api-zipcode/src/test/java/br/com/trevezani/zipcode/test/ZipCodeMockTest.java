package br.com.trevezani.zipcode.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.trevezani.commons.exception.BusinessException;
import br.com.trevezani.commons.exception.NotFoundException;
import br.com.trevezani.zipcode.dao.ZipCodeDao;
import br.com.trevezani.zipcode.service.ZipCodeService;

@ActiveProfiles("default")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ZipCodeMockTest {
	@InjectMocks
	private ZipCodeService zipCodeService;

	@Mock
	private ZipCodeDao zipCodeDao;

	@BeforeEach
	void setUp() throws Exception {
		Map<String, String> result = new HashMap<>();
		result.put("test", "mock");

		when(zipCodeDao.findByZip("111111")).thenReturn(result);
	}

	@DisplayName("Test Mock :: findByZip with Valid zip")
	@Test
	void test1() throws ValidationException, BusinessException, NotFoundException {
		assertTrue(zipCodeService.findByZip("111111").containsKey("test"));
		assertNotNull(zipCodeService.findByZip("111111").get("test"));
		assertEquals("mock", zipCodeService.findByZip("111111").get("test"));
	}

	@DisplayName("Test Mock :: findByZip with blank value")
	@Test
	void test2() {
		assertThrows(ValidationException.class, () -> {
			zipCodeService.findByZip("");
		});
	}

	@DisplayName("Test Mock :: findByZip with invalid value")
	@Test
	void test3() {
		assertThrows(NotFoundException.class, () -> {
			zipCodeService.findByZip("111112");
		});
	}
}
