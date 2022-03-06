package com.travix.medusa.busyflights;

import com.travix.medusa.busyflights.data.CrazyAirData;
import com.travix.medusa.busyflights.data.ToughJetAirData;
import com.travix.medusa.busyflights.repository.CrazyAirRepository;
import com.travix.medusa.busyflights.repository.ToughAirJetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@SpringBootApplication
public class BusyFlightsApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(BusyFlightsApplication.class);

	@Autowired
	private CrazyAirRepository crazyAirRepository;

	@Autowired
	private ToughAirJetRepository toughAirJetRepository;

	public static void main(String[] args) {
		SpringApplication.run(BusyFlightsApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Override
	public void run(String... args) {
		log.info("StartApplication...");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		//store data in crazyAirRespository
		log.info("Create crazy Air Respository");
		crazyAirRepository.save(new CrazyAirData("British Airways","LHR","LRT", "E",LocalDateTime.parse("2022-03-01 10:00:00", formatter),
				LocalDateTime.parse("2022-03-02 12:00:00", formatter), new BigDecimal(300 )));
		crazyAirRepository.save(new CrazyAirData("Frontier Airlines","LRT","LHR","E",LocalDateTime.parse("2022-06-01 00:00:00", formatter),
				LocalDateTime.parse("2022-06-02 00:00:00", formatter), new BigDecimal(427.50)));
		crazyAirRepository.save(new CrazyAirData("Frontier Airlines","LHR","UKY","B",LocalDateTime.parse("2022-03-01 00:00:00", formatter),
				LocalDateTime.parse("2022-04-01 00:00:00", formatter), new BigDecimal(500)));
		crazyAirRepository.save(new CrazyAirData("British Airways","UKY","LHR","E",LocalDateTime.parse("2022-03-01 00:00:00", formatter),
				LocalDateTime.parse("2022-03-01 00:00:00", formatter), new BigDecimal(256)));


		log.info("Create Tough Jet Air Respository");
		toughAirJetRepository.save(new ToughJetAirData("Easy Jet","LHR","LRT", LocalDateTime.parse("2022-03-01 10:00:00", formatter),
				LocalDateTime.parse("2022-03-02 12:00:00", formatter), new BigDecimal(300)));
		toughAirJetRepository.save(new ToughJetAirData("Emirates Airline","LRT","LHR",LocalDateTime.parse("2022-06-01 00:00:00", formatter),
				LocalDateTime.parse("2022-06-02 00:00:00", formatter), new BigDecimal(400)));
		toughAirJetRepository.save(new ToughJetAirData("Easy Jet","LHR","UKY",LocalDateTime.parse("2022-03-01 00:00:00", formatter),
				LocalDateTime.parse("2022-04-01 00:00:00", formatter), new BigDecimal(200)));
		toughAirJetRepository.save(new ToughJetAirData("Emirates Airline","LHR","MAA",LocalDateTime.parse("2022-07-01 00:00:00", formatter),
				LocalDateTime.parse("2022-08-01 00:00:00", formatter), new BigDecimal(350)));

	}

}
