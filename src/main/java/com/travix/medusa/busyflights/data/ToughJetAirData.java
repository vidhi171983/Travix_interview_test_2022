package com.travix.medusa.busyflights.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ToughJetAirData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String airLineName;
	private String origin;
	private String destination;
	private LocalDateTime departureDate;
	private LocalDateTime arrivalDate;
	@EqualsAndHashCode.Exclude
	private BigDecimal price;
	@Version
	private int versionNo = 1;

	public ToughJetAirData(String airLineName, String origin, String destination, LocalDateTime departureDate, LocalDateTime arrivalDate, BigDecimal price)
	{
		this.airLineName = airLineName;
		this.origin = origin;
		this.destination = destination;
		this.departureDate = departureDate;
		this.arrivalDate = arrivalDate;
		this.price = price;
	}

}
