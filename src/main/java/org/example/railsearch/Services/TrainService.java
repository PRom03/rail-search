package org.example.railsearch.Services;

import jakarta.transaction.Transactional;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {
    private final TrainRepository trainRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Transactional
    public void printAll() {
        List<Train> trains = trainRepository.findAll();
        for (Train train : trains) {
            System.out.println(train.getName());
            System.out.println(train.getInitStation());
            System.out.println(train.getFinStation());
        }
    }
        @Transactional
        public List<Train> getTrainsByStations(int id1, int id2){
            return trainRepository.getTrainsByStations(id1,id2);
            }

        public Train getTrainByName(String name){
            return trainRepository.getTrainByName(name);
        }
}



