package com.travix.medusa.busyflights.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ToughJetResponse extends FlightsResponse
{
    private String tax;
    private String discount;
}
