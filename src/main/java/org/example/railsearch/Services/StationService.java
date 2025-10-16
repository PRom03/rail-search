package org.example.railsearch.Services;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Repositories.StationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }
    public List<Station> getSuggestedStations(String query){
        return stationRepository.getSuggestedStations(query);
    }
}
