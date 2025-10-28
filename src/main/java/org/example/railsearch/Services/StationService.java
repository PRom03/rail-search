package org.example.railsearch.Services;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Repositories.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    public List<Station> getSuggestedStations(String query){
        return stationRepository.getSuggestedStations(query);
    }
}
