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
import java.time.LocalTime;
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

//        float sum = 0;
//
//
//        String stationFrom = "Siedlce";
//        String stationTo = "Lublin Główny";
//        List<List<Integer>> pairs=trainRepository.getTrainPairsByStations(stationRepository.getStationByName(stationFrom).getId(), stationRepository.getStationByName(stationTo).getId());
//        for (int i = 0; i < pairs.size(); i++) {
//            //System.out.println(pairs.get(i).get(0).+"\t"+pairs.get(i).get(1));
//            List<Integer> stops1=stationRepository.findStationsInStopsForTrain(pairs.get(i).get(0));
//            List<Integer> stops2=stationRepository.findStationsInStopsForTrain(pairs.get(i).get(1));
//
//            List<Integer> common = stops1.stream()
//                    .distinct()
//                    .filter(stops2::contains)
//                    .toList();
//            for (Integer stop : common) {
//                stationRepository.findById(Long.valueOf(stop)).ifPresent(station -> {
//                    System.out.println(station.getName());
//                });
//            }
//            System.out.println("----");
//        }
//trainService.getConns("Siedlce","Lublin Główny", LocalTime.now());
    }
}
