package com.travix.medusa.busyflights.repository;

import com.travix.medusa.busyflights.data.ToughJetAirData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ToughAirJetRepository extends CrudRepository<ToughJetAirData, Long>
{
   List<ToughJetAirData> findByOriginAndDestination(String from, String to);
}
