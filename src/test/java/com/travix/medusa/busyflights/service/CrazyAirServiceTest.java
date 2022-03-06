package com.travix.medusa.busyflights.service;

import com.travix.medusa.busyflights.data.CrazyAirData;
import com.travix.medusa.busyflights.repository.CrazyAirRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CrazyAirServiceTest {
	@LocalServerPort
	private int serverPort;

	@MockBean
	private CrazyAirRepository crazyAirRepository;

	@Before
	public void setup() {
		RestAssured.port = serverPort;
	}

	@Test
	public void testFindCrazyAirFlightFare() {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("origin", "LHR");
		queryParams.put("destination", "LRT");
		queryParams.put("departureDate", "2022-03-01");
		queryParams.put("returnDate", "2022-06-01");
		queryParams.put("passengerCount", 2);

		TestParams testParams = new TestParams();
		List<CrazyAirData> crazyAirDataList = testParams.createCrazyAirData();
		Mockito.when(crazyAirRepository.
				findByOriginAndDestination(queryParams.get("origin").toString(), queryParams.get("destination").toString())).thenReturn(crazyAirDataList);

		JsonPath jsonPath = given()
				.when()
				.accept(ContentType.JSON)
				.queryParams(queryParams)
				.get("/rest/v1/CrazyAirService")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.jsonPath();

		assertTrue(jsonPath.getList("airline").size() > 0);
		assertEquals(crazyAirDataList.get(0).getOrigin(), jsonPath.get("departureAirportCode[0]"));
		assertEquals(crazyAirDataList.get(0).getDestination(), jsonPath.get("destinationAirportCode[0]"));
		assertEquals(crazyAirDataList.get(0).getAirLineName(), jsonPath.get("airline[0]"));

	}

	@Test
	public void testFindCrazyAirForDiffDepartureDate() {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("origin", "LHR");
		queryParams.put("destination", "LRT");
		queryParams.put("departureDate", "2022-04-01");
		queryParams.put("returnDate", "2022-06-01");
		queryParams.put("passengerCount", 2);

		TestParams testParams = new TestParams();
		List<CrazyAirData> crazyAirDataList = testParams.createCrazyAirData_ForDiffDepartureDate();
		Mockito.when(crazyAirRepository.
				findByOriginAndDestination(queryParams.get("origin").toString(), queryParams.get("destination").toString())).thenReturn(crazyAirDataList);

		JsonPath jsonPath = given()
				.when()
				.accept(ContentType.JSON)
				.queryParams(queryParams)
				.get("/rest/v1/CrazyAirService")
				.then()
				.statusCode(HttpStatus.SC_OK)
				.extract()
				.jsonPath();

		//No flights for given departure date
		assertTrue(jsonPath.getList("airline").size() == 0);
		assertNotEquals(crazyAirDataList.get(0).getOrigin(), jsonPath.get("departureAirportCode[0]"));

	}

	@Test
	public void testFindCrazyAirForEmptyOrigin() {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("origin", "");
		queryParams.put("destination", "LRT");
		queryParams.put("departureDate", "2022-03-01");
		queryParams.put("returnDate", "2022-06-01");
		queryParams.put("passengerCount", 2);

		TestParams testParams = new TestParams();
		List<CrazyAirData> crazyAirDataList = testParams.createCrazyAirData();
		Mockito.when(crazyAirRepository.
				findByOriginAndDestination("LHR", queryParams.get("destination").toString())).thenReturn(crazyAirDataList);

		JsonPath jsonPath = given()
				.when()
				.accept(ContentType.JSON)
				.queryParams(queryParams)
				.get("/rest/v1/CrazyAirService")
				.then()
				.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
				.extract()
				.jsonPath();

		assertTrue(jsonPath.get("message").equals("Origin or Destination or Departure Date cannot be empty") );

	}


	class TestParams {
		List<CrazyAirData> createCrazyAirData() {
			List<CrazyAirData> crazyAirDatas = new ArrayList();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			crazyAirDatas.add(new CrazyAirData("British Airways", "LHR", "LRT", "E", LocalDateTime.parse("2022-03-01 10:00:00", formatter),
					LocalDateTime.parse("2022-03-02 12:00:00", formatter), new BigDecimal(300)));
			crazyAirDatas.add(new CrazyAirData("Frontier Airlines", "LRT", "LHR", "E", LocalDateTime.parse("2022-06-01 00:00:00", formatter),
					LocalDateTime.parse("2022-06-02 00:00:00", formatter), new BigDecimal(427.50)));
			return crazyAirDatas;
		}

		List<CrazyAirData> createCrazyAirData_ForDiffDepartureDate() {
			List<CrazyAirData> crazyAirDatas = new ArrayList();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			crazyAirDatas.add( new CrazyAirData("Frontier Airlines", "RRT", "LRT", "E", LocalDateTime.parse("2022-03-01 00:00:00", formatter),
					LocalDateTime.parse("2022-03-02 00:00:00", formatter), new BigDecimal(427.50)));

			return crazyAirDatas;
		}
	}
}
