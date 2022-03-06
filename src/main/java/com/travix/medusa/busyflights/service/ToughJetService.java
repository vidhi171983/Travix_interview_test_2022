package com.travix.medusa.busyflights.service;

import com.travix.medusa.busyflights.data.ToughJetAirData;
import com.travix.medusa.busyflights.domain.FlightsRequest;
import com.travix.medusa.busyflights.domain.FlightsResponse;
import com.travix.medusa.busyflights.domain.ToughJetResponse;
import com.travix.medusa.busyflights.repository.ToughAirJetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class ToughJetService implements FlightsService {
	private static final Logger log = LoggerFactory.getLogger(ToughJetService.class);
	private static final String TOUGH_JET = "Tough Jet";
	private static final String DISCOUNT = "5%";
	private static final String TAX = "7.5%";

	@Autowired
	private ToughAirJetRepository toughAirJetRepository;

	@GetMapping(value = "/ToughJetService")
	@Override
	public List<? extends FlightsResponse> getFare(FlightsRequest flightsRequest) {
		log.info("Searching Tough Jet Service");
		List<ToughJetResponse> toughJetResponses = new ArrayList<>();
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
			toughJetResponses.addAll(getFlightDetailsForDepartureDate(origin, destination, flightsRequest, departureDate));

			if (!flightsRequest.getReturnDate().isEmpty())
				toughJetResponses.addAll(getFlightDetailsForReturnDate(destination, origin, flightsRequest, returnDate));
			return toughJetResponses;
		} else {
			throw new RuntimeException("Origin or Destination or Departure Date cannot be empty");
		}

	}

	public List<ToughJetResponse> getFlightDetailsForDepartureDate(String origin, String destination, FlightsRequest flightsRequest, LocalDate departureDate) {
		List<ToughJetResponse> toughJetDepartureDateResponses = new ArrayList<>();
		CompletableFuture<List<ToughJetResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
			Predicate<ToughJetAirData> flightTime = c -> c.getDepartureDate().toLocalDate().isEqual(departureDate);
			Optional<List<ToughJetAirData>> toughJetDataDepartureList = Optional.ofNullable(toughAirJetRepository.findByOriginAndDestination(origin, destination)
					.stream().filter(flightTime).collect(Collectors.toList()));
			if (toughJetDataDepartureList.isPresent()) {
				for (ToughJetAirData toughJetAirData : toughJetDataDepartureList.get()) {
					ToughJetResponse toughJetResponse = new ToughJetResponse();
					toughJetResponse.setSupplier(TOUGH_JET);
					toughJetResponse.setAirline(toughJetAirData.getAirLineName());
					toughJetResponse.setArrivalDate(DateTimeFormatter.ISO_DATE_TIME.format(toughJetAirData.getArrivalDate()));
					toughJetResponse.setDepartureDate(DateTimeFormatter.ISO_DATE_TIME.format(toughJetAirData.getDepartureDate()));
					toughJetResponse.setFare(toughJetAirData.getPrice().multiply(new BigDecimal(flightsRequest.getPassengerCount())).setScale(2));
					toughJetResponse.setDepartureAirportCode(origin);
					toughJetResponse.setDestinationAirportCode(destination);
					toughJetResponse.setDiscount(DISCOUNT);
					toughJetResponse.setTax(TAX);
					toughJetDepartureDateResponses.add(toughJetResponse);
				}
			}
			return toughJetDepartureDateResponses;
		});
		try {
			return completableFuture.get();
		} catch (Exception e) {
			throw new RuntimeException("Error while fetching departure flight details from Tough Jet Service");
		}
	}

	public List<ToughJetResponse> getFlightDetailsForReturnDate(String origin, String destination, FlightsRequest flightsRequest, LocalDate returnDate) {
		List<ToughJetResponse> toughJetReturnDateResponses = new ArrayList<>();
		CompletableFuture<List<ToughJetResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
			Optional<List<ToughJetAirData>> toughJetAirDataList = Optional.ofNullable(toughAirJetRepository.findByOriginAndDestination(origin, destination)
					.stream().filter(c -> c.getDepartureDate().toLocalDate().isEqual(returnDate)).collect(Collectors.toList()));
			if (toughJetAirDataList.isPresent()) {
				for (ToughJetAirData toughJetAirData : toughJetAirDataList.get()) {
					ToughJetResponse toughJetResponse = new ToughJetResponse();
					toughJetResponse.setSupplier(TOUGH_JET);
					toughJetResponse.setAirline(toughJetAirData.getAirLineName());
					toughJetResponse.setArrivalDate(DateTimeFormatter.ISO_DATE_TIME.format(toughJetAirData.getArrivalDate()));
					toughJetResponse.setDepartureDate(DateTimeFormatter.ISO_DATE_TIME.format(toughJetAirData.getDepartureDate()));
					toughJetResponse.setFare(toughJetAirData.getPrice().multiply(new BigDecimal(flightsRequest.getPassengerCount())).setScale(2));
					//while returning origin and destination will be reversed
					toughJetResponse.setDepartureAirportCode(origin);
					toughJetResponse.setDestinationAirportCode(destination);
					toughJetResponse.setDiscount(DISCOUNT);
					toughJetResponse.setTax(TAX);
					toughJetReturnDateResponses.add(toughJetResponse);
				}
			}
			return toughJetReturnDateResponses;
		});
		try {
			return completableFuture.get();
		} catch (Exception e) {
			throw new RuntimeException("Error while fetching return flight details from Tough Jet Service");
		}
	}


}
