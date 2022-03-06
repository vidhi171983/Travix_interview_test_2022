package com.travix.medusa.busyflights.controller;

import com.travix.medusa.busyflights.domain.CrazyAirResponse;
import com.travix.medusa.busyflights.domain.FlightsRequest;
import com.travix.medusa.busyflights.domain.FlightsResponse;
import com.travix.medusa.busyflights.service.CrazyAirService;
import com.travix.medusa.busyflights.service.FlightsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/rest/v1")
public class BusyFlightController {
	private static final Logger log = LoggerFactory.getLogger(BusyFlightController.class);

	RestTemplate restTemplate;

	public BusyFlightController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping(value = "/BusyFlightService")
	public List<FlightsResponse> findFare(FlightsRequest flightsRequest)
	{
		log.info("Find Fare");
		List<FlightsResponse> flightsResponses = new ArrayList<>();
		flightsResponses.addAll(getCrazyAirFare(flightsRequest));
		flightsResponses.addAll(getToughJetFare(flightsRequest));
		return flightsResponses.stream().sorted(Comparator.comparing(FlightsResponse::getFare)).collect(Collectors.toList());

	}

	private List<FlightsResponse> getCrazyAirFare(FlightsRequest flightsRequest)  {
		log.info("Get Crazy Air Fare");
		String crazyAirResource = "http://localhost:8080/rest/v1/CrazyAirService?" +
				"origin={origin}&destination={destination}&departureDate={departureDate}&returnDate={returnDate}&passengerCount={passengerCount}";

		HttpHeaders headers = new HttpHeaders();
		HttpEntity httpEntity = new HttpEntity<>(headers);
		CompletableFuture<List<FlightsResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
			ResponseEntity<FlightsResponse[]> crazyAirResponse = restTemplate.exchange(crazyAirResource, HttpMethod.GET, httpEntity,
					FlightsResponse[].class, flightsRequest.getOrigin(),
					flightsRequest.getDestination(), flightsRequest.getDepartureDate(),
					flightsRequest.getReturnDate(), flightsRequest.getPassengerCount());
			return Arrays.asList(crazyAirResponse.getBody());
		});
		try
		{
			return completableFuture.get();
		}catch (Exception e)
		{
			throw new RuntimeException("Could not find fare from Crazy Air services");
		}
	}

	private List<FlightsResponse> getToughJetFare(FlightsRequest flightsRequest)
	{
		log.info("Get Tough Jet Fare");
		String toughJetResource = "http://localhost:8080/rest/v1/ToughJetService?" +
				"origin={origin}&destination={destination}&departureDate={departureDate}&returnDate={returnDate}&passengerCount={passengerCount}";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity httpEntity = new HttpEntity<>(headers);
		CompletableFuture<List<FlightsResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
			ResponseEntity<FlightsResponse[]> toughJetResponse = restTemplate.exchange(toughJetResource, HttpMethod.GET, httpEntity,
					FlightsResponse[].class, flightsRequest.getOrigin(),
					flightsRequest.getDestination(), flightsRequest.getDepartureDate(),
					flightsRequest.getReturnDate(), flightsRequest.getPassengerCount());
			return Arrays.asList(toughJetResponse.getBody());
		});
		try{
			return completableFuture.get();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not find fare from Tough Jet services");
		}

	}
}
