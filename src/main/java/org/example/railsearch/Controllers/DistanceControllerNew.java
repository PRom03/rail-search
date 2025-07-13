package org.example.railsearch.Controllers;



public class DistanceControllerNew {
    /*
    package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.StopService;
import org.example.railsearch.Services.TrainService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
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

    public record DistanceResponse(Integer distance) {
    }
    public record StationResponse(String name) {
    }
        // GET API to fetch all details
        @RequestMapping(value="/distance/{stationFrom}/{stationTo}",method= RequestMethod.GET,  produces="application/json")
        public DistanceResponse getDistance(@PathVariable String stationFrom, @PathVariable String stationTo) {
            return new DistanceResponse(Math.round(distanceService.findDistanceBetweenJunctionStations(stationFrom, stationTo)));
        }
    @RequestMapping(value="/distance-train/{stationFrom}/{stationTo}/{trainName}",method= RequestMethod.GET,  produces="application/json")
    public DistanceResponse getDistanceForTrain(@PathVariable String stationFrom, @PathVariable String stationTo,@PathVariable String trainName) {
        List<Stop> stops = stopService.findStopsByTrain(trainService.getTrainByName(trainName).getId(),stationRepository.getStationByName(stationFrom).getId(),stationRepository.getStationByName(stationTo).getId());
        float distance= 0.0F;
            for(int i=1;i<stops.size();i++) {
                Float dist_tmp = distanceService.findDistanceBetweenJunctionStations(stops.get(i - 1).getStation().getName(), stops.get(i).getStation().getName());
                String stationFromTmp = stops.get(i - 1).getStation().getName();
                String stationToTmp = stops.get(i).getStation().getName();
                if (dist_tmp != -1) distance += dist_tmp;
                else {
                    List<String> intermediateStations = new LinkedList<>();
                    Set<String> result =new HashSet<>();
                    while (result == null) {
                        List<String> station_from = distanceService.findNearestJunctionStations(stationFromTmp);
                        List<String> station_to = distanceService.findNearestJunctionStations(stationToTmp);
                        result = station_from.stream()
                                .distinct()
                                .filter(station_to::contains)
                                .collect(Collectors.toSet());
                        String res=result.iterator().next();

                        if (res!=null)intermediateStations.add(res);

                        stationFromTmp = ((LinkedList<String>)(intermediateStations)).getLast();
                    }


                    for (int j = 0; j < intermediateStations.size(); j++) {
                        distance += distanceService.findDistanceBetweenJunctionStations(stationFromTmp, intermediateStations.get(j));
                        distance += distanceService.findDistanceBetweenJunctionStations(intermediateStations.get(j), stationToTmp);
                        System.out.println(intermediateStations.get(j));
                    }
                }
            }
        return new DistanceResponse(Math.round(distance));
    }

    @RequestMapping(value="/nearest-junction-stations/{station}",method= RequestMethod.GET,  produces="application/json")
    public List<StationResponse> getNearestJunctionStations(@PathVariable String station) {
       List<String> stations= new ArrayList<>(distanceService.findNearestJunctionStations(station));
       List<StationResponse> stationResponse = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            stationResponse.add(new StationResponse(stations.get(i)));
        }
        return stationResponse;
    }

}


*/
}
