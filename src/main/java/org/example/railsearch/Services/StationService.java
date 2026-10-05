package org.example.railsearch.Services;

import org.example.railsearch.Entities.Distance;
import org.example.railsearch.Entities.Station;
import org.example.railsearch.Repositories.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    public List<Station> getSuggestedStations(String query){
        return stationRepository.getSuggestedStations(query);
    }
    public List<Station> findAll() {
        return stationRepository.findAll();
    }
        public Station createStation(String name) {
            Station station = new Station();
            Station existing =stationRepository.getStationByName(name);
            if(existing==null)station.setName(name);
            else throw new RuntimeException("Stacja o takiej nazwie już istnieje.");
            station.setName(name);
            return stationRepository.save(station);
        }
        public Station updateStation(long id,String name) {

            Station station=stationRepository.findById(id).orElseThrow()  ;
            Station existing =stationRepository.getStationByName(name);
            if(existing==null)station.setName(name);
            else throw new RuntimeException("Stacja o takiej nazwie już istnieje.");
            station.setName(name);
            return stationRepository.save(station);
        }
        public Station findById(long id) {
            return stationRepository.findById(id).orElseThrow();
        }
        public void deleteById(long id) {
            Station station = stationRepository.findById(id).orElseThrow();

            stationRepository.delete(station);
        }

}
