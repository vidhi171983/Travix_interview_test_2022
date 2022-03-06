package com.travix.medusa.busyflights;

import com.travix.medusa.busyflights.data.CrazyAirData;
import com.travix.medusa.busyflights.data.ToughJetAirData;
import com.travix.medusa.busyflights.domain.FlightsResponse;
import com.travix.medusa.busyflights.repository.CrazyAirRepository;
import com.travix.medusa.busyflights.repository.ToughAirJetRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BusyFlightsApplicationTests {
	@LocalServerPort
	private int serverPort;

	@Autowired
	private CrazyAirRepository crazyAirRepository;

	@Autowired
	private ToughAirJetRepository toughAirJetRepository;

	@Mock
	private RestTemplate restTemplate;

	@Before
	public void setup() {
		RestAssured.port = serverPort;
	}

	@Test
	public void testRestExchangeForCrazyAirService() {

		String origin = "LHR";
		String destination = "LRT";
		String departureDate = "2022-03-01";
		String returnDate = "2022-06-01";

		String crazyAirResource = "http://localhost:8080/rest/v1/CrazyAirService?" +
				"origin={origin}&destination={destination}&departureDate={departureDate}&returnDate={returnDate}&passengerCount={passengerCount}";

		HttpHeaders headers = new HttpHeaders();
		HttpEntity httpEntity = new HttpEntity<>(headers);

		FlightsResponse[] flightsResponse = new FlightsResponse[2];
		flightsResponse[0] = new FlightsResponse();
		flightsResponse[0].setDepartureAirportCode(origin);
		flightsResponse[0].setDestinationAirportCode(destination);

		FlightsResponse[] flightsResponses = flightsResponse;
		Mockito.when(
				restTemplate.exchange(crazyAirResource, HttpMethod.GET, httpEntity, FlightsResponse[].class,
						origin, destination,
						departureDate,
						returnDate, 2)).thenReturn(new ResponseEntity(flightsResponses, org.springframework.http.HttpStatus.valueOf(HttpStatus.SC_OK)));

		List<CrazyAirData> crazyAirData = crazyAirRepository.findByOriginAndDestination(origin, destination);
		assertEquals(crazyAirData.get(0).getOrigin(), flightsResponse[0].getDepartureAirportCode());

	}

	@Test
	public void testRestExchangeForToughJetService() {
		String origin = "LHR";
		String destination = "LRT";
		String departureDate = "2022-03-01";
		String returnDate = "2022-06-01";

		String toughJetResource = "http://localhost:8080/rest/v1/ToughJetService?" +
				"origin={origin}&destination={destination}&departureDate={departureDate}&returnDate={returnDate}&passengerCount={passengerCount}";

		HttpHeaders headers = new HttpHeaders();
		HttpEntity httpEntity = new HttpEntity<>(headers);

		FlightsResponse[] flightsResponse = new FlightsResponse[2];
		flightsResponse[0] = new FlightsResponse();
		flightsResponse[0].setDepartureAirportCode(origin);
		flightsResponse[0].setDestinationAirportCode(destination);

		FlightsResponse[] flightsResponses = flightsResponse;
		Mockito.when(
				restTemplate.exchange(toughJetResource, HttpMethod.GET, httpEntity, FlightsResponse[].class,
						origin, destination,
						departureDate,
						returnDate, 2)).thenReturn(new ResponseEntity(flightsResponses, org.springframework.http.HttpStatus.valueOf(HttpStatus.SC_OK)));

		List<ToughJetAirData> toughJetAirData = toughAirJetRepository.findByOriginAndDestination(origin, destination);
		assertEquals(toughJetAirData.get(0).getOrigin(), flightsResponse[0].getDepartureAirportCode());


	}
}
