package org.example.railsearch;


import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.StopRepository;
import org.example.railsearch.Repositories.TrainRepository;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.StopService;
import org.example.railsearch.Services.TrainService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaRepositories
public class RailSearchApplication implements CommandLineRunner {

    private final TrainService trainService;
    private final StopService stopService;
    private final DistanceService distanceService;
    private final TrainRepository trainRepository;
    private final StopRepository stopRepository;
    private final DistanceRepository distanceRepository;
    private final StationRepository stationRepository;

    public RailSearchApplication(TrainService trainService, TrainRepository trainRepository, StopService stopService, DistanceService distanceService, StopRepository stopRepository, DistanceRepository distanceRepository, StationRepository stationRepository) {
        this.trainService = trainService;
        this.trainRepository = trainRepository;
        this.stopService = stopService;
        this.distanceService = distanceService;
        this.stopRepository = stopRepository;
        this.distanceRepository = distanceRepository;
        this.stationRepository = stationRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(RailSearchApplication.class, args);
    }
    @Override
    public void run(String... args) {

        float sum = 0;


        String stationFrom = "Warszawa Wschodnia";
        String stationTo = "Iława Główna";

        //System.out.println(trainService.getTrainByName("IC 12104 CZECHOWICZ").getId());
        List<String> pendolinoJunctions=new ArrayList<>(Arrays.asList(
                "Warszawa Praga","Legionowo","Nasielsk","Działdowo"));
        for (int i = 1; i < pendolinoJunctions.size(); i++) {
            sum+= distanceService.findDistanceBetweenJunctionStations(pendolinoJunctions.get(i-1), pendolinoJunctions.get(i));

        }
        sum+=distanceService.findDistanceBetweenJunctionStations(stationFrom, pendolinoJunctions.get(0))+distanceService.findDistanceBetweenJunctionStations(pendolinoJunctions.get(pendolinoJunctions.size()-1), stationTo);
        System.out.println(sum);

    }
}
