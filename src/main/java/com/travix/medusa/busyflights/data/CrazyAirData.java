package com.travix.medusa.busyflights.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class CrazyAirData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String airLineName;
    @NotNull
    private String origin;
    @NotNull
    private String destination;
    @NotNull
    @EqualsAndHashCode.Exclude
    private String cabinClass;
    @NotNull
    private LocalDateTime departureDate;
    @NotNull
    private LocalDateTime arrivalDate;
    @NotNull
    @EqualsAndHashCode.Exclude
    private BigDecimal price;
    @Version
    private int versionNo = 1;

    public CrazyAirData(String airLineName,String origin, String destination, String cabinClass,LocalDateTime departureDate,
                        LocalDateTime arrivalDate, BigDecimal price) {
        this.airLineName = airLineName;
        this.origin = origin;
        this.destination = destination;
        this.cabinClass = cabinClass;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.price = price;
    }

}
