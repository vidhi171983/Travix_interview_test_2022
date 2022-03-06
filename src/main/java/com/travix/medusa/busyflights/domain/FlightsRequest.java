package com.travix.medusa.busyflights.domain;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FlightsRequest {

    @NotNull(message = "Destination is required.")
    private String origin;
     @NotNull(message = "Destination is required.")
    private String destination;
    @NotNull(message = "Departure date is required.")
    private String departureDate;
    private String returnDate;
    @NotNull(message = "Passenger count is required.")
    private int passengerCount;

}
