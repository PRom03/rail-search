package org.example.railsearch;


import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.StopRepository;
import org.example.railsearch.Repositories.TrainRepository;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.StopService;
import org.example.railsearch.Services.TrainServiceOld;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories
public class RailSearchApplication implements CommandLineRunner {

    private final TrainServiceOld trainService;
    private final StopService stopService;
    private final DistanceService distanceService;
    private final TrainRepository trainRepository;
    private final StopRepository stopRepository;
    private final DistanceRepository distanceRepository;
    private final StationRepository stationRepository;

    public RailSearchApplication(TrainServiceOld trainService, TrainRepository trainRepository, StopService stopService, DistanceService distanceService, StopRepository stopRepository, DistanceRepository distanceRepository, StationRepository stationRepository) {
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


    }
}
