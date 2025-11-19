package org.example.railsearch.Services;

import org.example.railsearch.Repositories.TrainDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class TrainDateService {
    @Autowired
    private TrainDateRepository trainDateRepository;
    public Integer getIdByTrainIdAndDate(int trainId, LocalDate date) {

        return trainDateRepository.getIdByTrainIdAndDate(trainId, date);
    }
}
