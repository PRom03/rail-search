package org.example.railsearch.Services;

import org.example.railsearch.Entities.Distance;
import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistanceService {
    @Autowired
    private DistanceRepository distanceRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private StopService stopService;
    @Autowired
    private TrainServiceOld trainService;
    @Autowired
    private TrainRepository trainRepository;




    public Distance findDistanceBetweenNeighboring(int id1, int id2) {
        return distanceRepository.findDistanceBetweenNeighboring(id1,id2);

    }

    public Pair<Float,List<String>> Dijkstra(String stationFrom,String stationTo) {
        List<Station> graph=stationRepository.findAll();
        HashMap<Station,Float> dist_=new HashMap<>();
        HashMap<Station,Float> dist=new HashMap<>();
        dist.put(stationRepository.findStationByName(stationFrom),0.0F);
        dist_.put(stationRepository.findStationByName(stationFrom),0.0F);
        for (var v :graph) {
            if (v.getName().compareTo(stationFrom)!=0) {
                dist.put(v, 1500.0F);
                dist_.put(v, 1500.0F);
            }
        }
        Float found=null;
        List<String> intermediate=new ArrayList<>();
        while (found==null) {
            Map.Entry<Station,Float> u = null;
            for (Map.Entry<Station,Float> entry : dist_.entrySet()) {
                if (u == null || entry.getValue() < u.getValue()) {
                    u= entry;
                }
            }
            assert u != null;
            dist_.remove(u.getKey());
            var curr=u.getKey();
            List<Station> neighbors=stationRepository.findNeighboringStations((curr.getId()));
            for(var neighbor:neighbors) {
                if(dist.get(neighbor)==0.0F&&!intermediate.contains(neighbor.getName())) {
                    intermediate.add(neighbor.getName());
                }
            }
            for(var neighbor:neighbors) {
                Float dstnc=findDistanceBetweenNeighboring(neighbor.getId(),curr.getId()).getDistance().floatValue();
                float alt = dist.get(curr) + dstnc;
                if(dist.get(neighbor)==null) continue;
                if (alt < dist.get(neighbor)) {
                    dist.put(neighbor, alt);
                    dist_.put(neighbor,alt);
                    if(neighbor.getName().equals(stationTo)) {
                        found=dist.get(neighbor);
                        break;
                    }
                }
            }
        }
        return Pair.of(found,intermediate);
    }



    public Float getDistanceForTrain(String stationFrom, String stationTo, String trainName){
        ArrayList<Stop> stops = (ArrayList<Stop>) stopService.findStopsByTrain(trainService.getTrainByName(trainName).getId(),stationRepository.getStationByName(stationFrom).getId(),stationRepository.getStationByName(stationTo).getId());
        float distance= 0.0F;
        System.out.println(stops.size());
        for(int i=1;i<stops.size();i++){

                distance+=Dijkstra(stops.get(i-1).getStation().getName(),stops.get(i).getStation().getName()).getFirst();
                List<String> intermediate=Dijkstra(stops.get(i-1).getStation().getName(),stops.get(i).getStation().getName()).getSecond();
                for(int j=0;j<intermediate.size();j++){
                  //  System.out.println(intermediate.get(j));
                }

        }
        return distance;
    }
    public List<List<Train>> getTrains(String stationFrom, String stationTo){;
        List<String> route=Dijkstra(stationFrom,stationTo).getSecond();
        List <List<Train>> trains=new ArrayList<>();
        for (int i=1;i<route.size();i++){
            trains.add(trainRepository.getTrainsByStations(stationRepository.getStationByName(route.get(i-1)).getId(),stationRepository.getStationByName(route.get(i)).getId()));
        }
        return trains.stream().distinct().collect(Collectors.toList());
    }


}
