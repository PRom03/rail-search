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
Set<String> result=null;
                ArrayList<String> intermediateStation=new ArrayList<>();

                while(result==null) {
                    ArrayList<Station> station_from= new ArrayList<Station>(findNearestJunctionStations(stationFromTmp));
                    ArrayList<Station> station_to= new ArrayList<Station>(findNearestJunctionStations(stationToTmp));
                    for(var s:station_from) {
                        System.out.print(s.getName()+", ");
                    }
                    System.out.println();
                    for(var s:station_to) {
                        System.out.print(s.getName()+", ");
                    }
                    System.out.println();

                    ArrayList<Station> finalStation_to = station_to;
                    result=station_from.stream()
                            .map(Station::getName)                    // ekstrakcja nazw stacji
                            .distinct()
                            .filter(name -> finalStation_to.stream()       // porównanie po nazwach
                                    .map(Station::getName)
                                    .anyMatch(name::equals))
                            .collect(Collectors.toSet());
                    if(result!=null && !result.isEmpty()) {
                        intermediateStation.add(result.iterator().next());
                        for (var iS : intermediateStation) {
                           // System.out.println(iS);
                        }
                    }
                    else {
                        ArrayList<Station> station_from_tmp=new ArrayList<>(station_from);
                        ArrayList<Station> station_to_tmp=new ArrayList<>(station_to);
                        for (int j = 0; j < station_from_tmp.size(); j++) {
                            if(findDistanceBetweenJunctionStations(station_from_tmp.get(j).getName(),stationFromTmp)!=-1&&distanceRepository.findDistancesByStationName(station_from_tmp.get(j).getName()).size()==2) {
                                station_from_tmp.remove(j);
                            }
                        }
                        for (int j = 0; j < station_to_tmp.size(); j++) {
                            if (findDistanceBetweenJunctionStations(station_to_tmp.get(j).getName(), stationToTmp) != -1&&distanceRepository.findDistancesByStationName(station_to_tmp.get(j).getName()).size()==2) {
                                station_to_tmp.remove(j);
                            }
                        }
                        System.out.println("------");
                        for(var s:station_from_tmp) {
                            System.out.println(s.getName());
                        }
                        for(var toTmp:station_to_tmp) {
                            System.out.println(toTmp.getName());
                        }
                        if(station_from_tmp.size()< station_to_tmp.size()) {
                            for (int j = 0; j < station_from_tmp.size(); j++) {
                                for (int k = 0; k < station_to_tmp.size(); k++) {

                                    if (findDistanceBetweenJunctionStations(station_to_tmp.get(k).getName(), station_from_tmp.get(j).getName()) != -1) {
                                        station_to = new ArrayList<>(findNearestJunctionStations(station_to_tmp.get(k).getName()));
                                        station_from = new ArrayList<>(findNearestJunctionStations(station_from_tmp.get(j).getName()));
                                        intermediateStation.add(station_from_tmp.get(j).getName());
                                        intermediateStation.add(station_to_tmp.get(k).getName());
                                    }
                                }
                            }
                        }
                        else{
                            for (int j = 0; j < station_to_tmp.size(); j++) {
                                for (int k = 0; k < station_from_tmp.size(); k++) {

                                    if (findDistanceBetweenJunctionStations(station_to_tmp.get(j).getName(), station_from_tmp.get(k).getName()) != -1) {
                                        station_to = new ArrayList<>(findNearestJunctionStations(station_to_tmp.get(j).getName()));
                                        station_from = new ArrayList<>(findNearestJunctionStations(station_from_tmp.get(k).getName()));
                                        intermediateStation.add(station_from_tmp.get(k).getName());
                                        intermediateStation.add(station_to_tmp.get(j).getName());
                                    }
                                }
                            }
                        }
                    }
                }
                if(!intermediateStation.isEmpty()) {
                    for (var iS : intermediateStation) {
                        System.out.println("i "+iS);
                    }
                    System.out.println("------");
                    float tmp =0.0F;
                    for (int j = 1; j < intermediateStation.size(); j++) {
                        tmp+=findDistanceBetweenJunctionStations(intermediateStation.get(j-1), intermediateStation.get(j));
                    }
                    System.out.println(stationFromTmp+"-"+intermediateStation.get(0)+" "+intermediateStation.get(intermediateStation.size()-1)+"-"+stationToTmp);
                    float tmp_ = tmp+findDistanceBetweenJunctionStations(stationFromTmp, intermediateStation.get(0)) + findDistanceBetweenJunctionStations(intermediateStation.get(intermediateStation.size()-1), stationToTmp);
                    System.out.println(tmp_);
                    distance += tmp_;
                }

*/
}
