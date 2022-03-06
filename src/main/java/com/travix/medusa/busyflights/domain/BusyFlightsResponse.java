package com.travix.medusa.busyflights.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BusyFlightsResponse
{
	private String airline;
	private String supplier;
	private BigDecimal fare;
	private String departureAirportCode;
	private String destinationAirportCode;
	private String departureDate;
	private String arrivalDate;
}
