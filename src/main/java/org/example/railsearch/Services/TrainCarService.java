package org.example.railsearch.Services;

import org.example.railsearch.Repositories.TrainCarRepository;
import org.example.railsearch.Entities.TrainCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainCarService {
    @Autowired
    private TrainCarRepository trainCarRepository;
    public List<TrainCar> findByTrainId(Long trainId) {
        return trainCarRepository.findByTrainId(trainId);
    }
}
