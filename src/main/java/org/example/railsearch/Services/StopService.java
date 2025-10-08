package org.example.railsearch.Services;


import jakarta.transaction.Transactional;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Repositories.StopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StopService {
    private final StopRepository stopRepository;

    public StopService(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    @Transactional
    public List<Stop> findStopsByTrain(int id,int stationFromId,int stationToId) {
        return stopRepository.findStopsByTrain(id, stationFromId, stationToId);


    }
}
