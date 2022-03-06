package com.travix.medusa.busyflights.repository;

import com.travix.medusa.busyflights.data.CrazyAirData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CrazyAirRepository extends CrudRepository<CrazyAirData, Long>
{
	List<CrazyAirData> findByOriginAndDestination(String origin, String destination );
}
