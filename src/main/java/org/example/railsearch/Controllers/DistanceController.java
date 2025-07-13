package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.StopService;
import org.example.railsearch.Services.TrainService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DistanceController {

private final DistanceService distanceService;
    private final StopService stopService;
private final TrainService trainService;
private final DistanceRepository distanceRepository;
private final StationRepository stationRepository;

public DistanceController(DistanceService distanceService, StopService stopService, TrainService trainService, DistanceRepository distanceRepository, StationRepository stationRepository) {
    this.distanceService = distanceService;
    this.stopService = stopService;
    this.trainService = trainService;
    this.distanceRepository = distanceRepository;
    this.stationRepository = stationRepository;
}

    public record DistanceResponse(Integer distance,String message) {
    }
    public record StationResponse(String name) {
    }
        // GET API to fetch all details
        @RequestMapping(value="/distance/{stationFrom}/{stationTo}",method= RequestMethod.GET,  produces="application/json")
        public DistanceResponse getDistance(@PathVariable String stationFrom, @PathVariable String stationTo) {
            int distance=Math.round(distanceService.findDistanceBetweenJunctionStations(stationFrom, stationTo));
            return new DistanceResponse(distance,(distance!=-1)?"success":"fail");
        }
    @RequestMapping(value="/distance-train-test/{stationFrom}/{stationTo}",method= RequestMethod.GET,  produces="application/json")
    public DistanceResponse getDistanceForTrainTest(@PathVariable String stationFrom, @PathVariable String stationTo) {
        int distance=Math.round(distanceService.Dijkstra(stationFrom, stationTo));
        return new DistanceResponse(distance,(distance!=-1)?"success":"fail");
    }
    @RequestMapping(value="/distance-train/{stationFrom}/{stationTo}/{trainName}",method= RequestMethod.GET,  produces="application/json")
    public DistanceResponse getDistanceForTrain(@PathVariable String stationFrom, @PathVariable String stationTo,@PathVariable String trainName) {
        int distance=Math.round(distanceService.getDistanceForTrain(stationFrom, stationTo, trainName));
        return new DistanceResponse(distance,(distance!=-1)?"success":"fail");
    }

    @RequestMapping(value="/nearest-junction-stations/{station}",method= RequestMethod.GET,  produces="application/json")
    public List<StationResponse> getNearestJunctionStations(@PathVariable String station) {
       List<Station> stations= new ArrayList<>(distanceService.findNearestJunctionStations(station));
       List<StationResponse> stationResponse = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            stationResponse.add(new StationResponse(stations.get(i).getName()));
        }
        return stationResponse;
    }

}
