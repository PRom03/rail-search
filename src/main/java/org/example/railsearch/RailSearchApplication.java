package org.example.railsearch;


import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.checkerframework.checker.units.qual.A;
import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.StopRepository;
import org.example.railsearch.Repositories.TrainRepository;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.StopService;
//import org.example.railsearch.Services.TrainServiceOld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.JavaVersion;
import org.springframework.core.SpringVersion;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;


@SpringBootApplication
@EnableJpaRepositories
public class RailSearchApplication implements CommandLineRunner {

    private final StopService stopService;
    private final DistanceService distanceService;
    private final TrainRepository trainRepository;
    private final StopRepository stopRepository;
    private final DistanceRepository distanceRepository;
    private final StationRepository stationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public RailSearchApplication( TrainRepository trainRepository, StopService stopService, DistanceService distanceService, StopRepository stopRepository, DistanceRepository distanceRepository, StationRepository stationRepository) {
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
        System.out.println("spring version: "+ SpringVersion.getVersion());
        System.out.println("java version: "+JavaVersion.getJavaVersion().toString());
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.println(Encoders.BASE64.encode(key.getEncoded()));
        System.out.println(passwordEncoder.encode("?6CDqnbFG6b2W=~"));
    }
}
