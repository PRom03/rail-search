package org.example.railsearch.Services;


import jakarta.transaction.Transactional;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Repositories.StopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StopService {
    @Autowired
    private StopRepository stopRepository;


    @Transactional
    public List<Stop> findStopsByTrain(int id,int stationFromId,int stationToId) {
        return stopRepository.findStopsByTrain(id, stationFromId, stationToId);


    }
}
