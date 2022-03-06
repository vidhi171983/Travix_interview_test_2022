package com.travix.medusa.busyflights.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class FlightsResponse implements Serializable
{
	private static final long serialVersionUID = 1234567L;
	private String airline;
	private String supplier;
	private BigDecimal fare;
	private String departureAirportCode;
	private String destinationAirportCode;
	private String departureDate;
	private String arrivalDate;

}
