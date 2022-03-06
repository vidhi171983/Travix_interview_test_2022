package com.travix.medusa.busyflights.service;

import com.travix.medusa.busyflights.domain.FlightsRequest;
import com.travix.medusa.busyflights.domain.FlightsResponse;

import java.time.LocalDate;
import java.util.List;


public interface FlightsService
{
     List<? extends FlightsResponse> getFare ( FlightsRequest flightsRequest) ;
     List<? extends FlightsResponse> getFlightDetailsForDepartureDate(String origin, String destination, FlightsRequest flightsRequest, LocalDate departureDate);
     List<? extends FlightsResponse> getFlightDetailsForReturnDate(String origin, String destination, FlightsRequest flightsRequest, LocalDate returnDate) ;
}
