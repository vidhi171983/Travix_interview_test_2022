package com.travix.medusa.busyflights.service;

import antlr.StringUtils;
import com.travix.medusa.busyflights.data.CrazyAirData;
import com.travix.medusa.busyflights.domain.FlightsRequest;
import com.travix.medusa.busyflights.domain.CrazyAirResponse;
import com.travix.medusa.busyflights.repository.CrazyAirRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest/v1")
public class CrazyAirService implements FlightsService {

	private static final Logger log = LoggerFactory.getLogger(CrazyAirService.class);
	private static final String CRAZY_AIR = "Crazy Air";

	@Autowired
	private CrazyAirRepository crazyAirRepository;

	@GetMapping(value = "/CrazyAirService")
	@Override
	public List<CrazyAirResponse> getFare( FlightsRequest flightsRequest) {
		log.info("Searching Crazy Air Service");
		List<CrazyAirResponse> crazyAirResponses = new ArrayList<>();
		String origin = flightsRequest.getOrigin();
		String destination = flightsRequest.getDestination();
		String departure = flightsRequest.getDepartureDate();
		LocalDate departureDate;

		if ( !origin.isEmpty() && !destination.isEmpty() && !departure.isEmpty()) {
			LocalDate returnDate;
			try {
				departureDate = LocalDate.parse(departure, DateTimeFormatter.ISO_LOCAL_DATE);
				returnDate = flightsRequest.getReturnDate().isEmpty() ? null : LocalDate.parse(flightsRequest.getReturnDate(), DateTimeFormatter.ISO_LOCAL_DATE);
			} catch (Exception e) {
				throw new RuntimeException("Could not parse given dates. Please enter correct dates");
			}
			crazyAirResponses.addAll(getFlightDetailsForDepartureDate(origin, destination, flightsRequest, departureDate));

			if ( !flightsRequest.getReturnDate().isEmpty())
				crazyAirResponses.addAll(getFlightDetailsForReturnDate(destination, origin, flightsRequest, returnDate));
			return crazyAirResponses;
		} else {
			throw new RuntimeException("Origin or Destination or Departure Date cannot be empty");
		}

	}

	public List<CrazyAirResponse> getFlightDetailsForDepartureDate(String origin, String destination, FlightsRequest flightsRequest, LocalDate departureDate)
	{
		List<CrazyAirResponse> crazyAirDepartureDateResponses = new ArrayList<>();
		CompletableFuture<List<CrazyAirResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
			Predicate<CrazyAirData> flightTime = c -> c.getDepartureDate().toLocalDate().isEqual(departureDate);
			Optional<List<CrazyAirData>> crazyAirDataDepartureList = Optional.ofNullable(crazyAirRepository.findByOriginAndDestination(origin, destination)
					.stream().filter(flightTime).collect(Collectors.toList()));
			if (crazyAirDataDepartureList.isPresent())
			{
				for (CrazyAirData crazyAirData : crazyAirDataDepartureList.get()) {
					CrazyAirResponse crazyAirResponse = new CrazyAirResponse();
					crazyAirResponse.setSupplier(CRAZY_AIR);
					crazyAirResponse.setAirline(crazyAirData.getAirLineName());
					crazyAirResponse.setCabinclass(crazyAirData.getCabinClass());
					crazyAirResponse.setArrivalDate(DateTimeFormatter.ISO_DATE_TIME.format(crazyAirData.getArrivalDate()));
					crazyAirResponse.setDepartureDate(DateTimeFormatter.ISO_DATE_TIME.format(crazyAirData.getDepartureDate()));
					crazyAirResponse.setFare(crazyAirData.getPrice().multiply(new BigDecimal(flightsRequest.getPassengerCount())).setScale(2));
					crazyAirResponse.setDepartureAirportCode(origin);
					crazyAirResponse.setDestinationAirportCode(destination);
					crazyAirDepartureDateResponses.add(crazyAirResponse);

				}
			}
			return crazyAirDepartureDateResponses;
		});
		try {
			return completableFuture.get();
		} catch (Exception e) {
			throw new RuntimeException("Error while fetching departure flight details from Crazy Air Service");
		}
	}

	public List<CrazyAirResponse> getFlightDetailsForReturnDate(String origin, String destination, FlightsRequest flightsRequest, LocalDate returnDate)
	{
		List<CrazyAirResponse> crazyAirReturnDateResponses = new ArrayList<>();
		CompletableFuture<List<CrazyAirResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
			Optional<List<CrazyAirData>> crazyAirDataReturnList = Optional.ofNullable(crazyAirRepository.findByOriginAndDestination(origin, destination)
					.stream().filter(c -> c.getDepartureDate().toLocalDate().isEqual(returnDate)).collect(Collectors.toList()));
			if (crazyAirDataReturnList.isPresent()) {
				for (CrazyAirData crazyAirData : crazyAirDataReturnList.get()) {
					CrazyAirResponse crazyAirResponse = new CrazyAirResponse();
					crazyAirResponse.setSupplier(CRAZY_AIR);
					crazyAirResponse.setAirline(crazyAirData.getAirLineName());
					crazyAirResponse.setCabinclass(crazyAirData.getCabinClass());
					crazyAirResponse.setArrivalDate(DateTimeFormatter.ISO_DATE_TIME.format(crazyAirData.getArrivalDate()));
					crazyAirResponse.setDepartureDate(DateTimeFormatter.ISO_DATE_TIME.format(crazyAirData.getDepartureDate()));
					crazyAirResponse.setFare(crazyAirData.getPrice().multiply(new BigDecimal(flightsRequest.getPassengerCount())).setScale(2));
					crazyAirResponse.setDepartureAirportCode(origin);
					crazyAirResponse.setDestinationAirportCode(destination);
					crazyAirReturnDateResponses.add(crazyAirResponse);
				}
			}
			return crazyAirReturnDateResponses;
		});
		try {
			return completableFuture.get();
		} catch (Exception e) {
			throw new RuntimeException("Error while fetching return flight details from Crazy Air Service");
		}
	}
}
