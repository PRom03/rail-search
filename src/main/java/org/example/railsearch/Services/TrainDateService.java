package org.example.railsearch.Services;

import org.example.railsearch.Entities.Train;
import org.example.railsearch.Entities.TrainDate;
import org.example.railsearch.Repositories.TrainDateRepository;
import org.example.railsearch.Repositories.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class TrainDateService {
    @Autowired
    private TrainDateRepository trainDateRepository;
    @Autowired
    private TrainRepository trainRepository;
    public Integer getIdByTrainIdAndDate(int trainId, LocalDate date) {

        return trainDateRepository.getIdByTrainIdAndDate(trainId, date);
    }public record TrainDateRequest (Long trainId, LocalDate date) {}



    public void addMany(List<TrainDateRequest> requests) {

        for (TrainDateRequest r : requests) {

            Train train = trainRepository.findById(r.trainId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Nie ma pociągu: " + r.trainId())
                    );

            boolean exists = trainDateRepository.existsByTrainAndDate(train, r.date());

            if (exists) continue;

            TrainDate td = new TrainDate();
            td.setTrain(train);
            td.setDate(r.date());

            trainDateRepository.save(td);
        }
    }

}
