package org.example.railsearch.Services;

import jakarta.transaction.Transactional;
import org.example.railsearch.Entities.Distance;
import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.TrainRepository;
import org.javatuples.Triplet;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistanceService {
    private final DistanceRepository distanceRepository;
    private final StationRepository stationRepository;
    private final StopService stopService;
    private final TrainService trainService;
    private final TrainRepository trainRepository;

    public DistanceService(DistanceRepository distanceRepository, StationRepository stationRepository, StopService stopService, TrainService trainService, TrainRepository trainRepository, TrainRepository trainRepository1) {
        this.distanceRepository = distanceRepository;
        this.stationRepository = stationRepository;

        this.stopService = stopService;
        this.trainService = trainService;
        this.trainRepository = trainRepository1;
    }


    public Distance findDistanceBetweenNeighboring(int id1, int id2) {
        return distanceRepository.findDistanceBetweenNeighboring(id1,id2);

    }
    public List<Station> findJunctionStations() {
        List<Station> stations=stationRepository.findAll();
        int count=0;
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            int neighboring = findDistancesByStationName(station.getName()).size();
            if (neighboring > 2) count += neighboring;
            else {
                stations.remove(station);
            }
        }
        return stations;
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
                    //System.out.println(neighbor.getName());
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
    public ArrayList<Station> findNearestJunctionStations(String stationName){
        String station1 = stationName;
        List<Distance> distancesForStation = new ArrayList<>(distanceRepository.findDistancesByStationName(station1));
        String station2="";
        ArrayList<Station> junctionStations=new ArrayList<>();
        for (int i = 0; i < distancesForStation.size(); i++) {
            station1=stationName;
            if (distancesForStation.get(i).getStation1().getName().equals(station1)) {
                station2 = distancesForStation.get(i).getStation2().getName();
            } else if (distancesForStation.get(i).getStation2().getName().equals(station1)) {
                station2 = distancesForStation.get(i).getStation1().getName();
            }
            while(distanceRepository.findDistancesByStationName(station2).size()==2) {
                Distance currentDistance = null;
                currentDistance = distanceRepository.findNextDistance(station2, station1);
                Pair<String, String> newStationPair = findNextStationPair(station1, station2, currentDistance);
                station1 = newStationPair.getFirst();
                station2 = newStationPair.getSecond();
            }
            junctionStations.add(stationRepository.findStationByName(station2));
        }
        return junctionStations;

    }
    public Pair<String,String> findNextStationPair(String station1, String station2,Distance currentDistance) {

        if (station2.compareTo(currentDistance.getStation1().getName()) == 0) {
            station1 = currentDistance.getStation1().getName();
            station2 = currentDistance.getStation2().getName();
        } else {
            station2 = currentDistance.getStation1().getName();
            station1 = currentDistance.getStation2().getName();
        }
        return Pair.of(station1,station2);
    }
    public Float findDistanceBetweenJunctionStations(String stationFrom, String stationTo) {
        String station1=stationFrom;
        String station2="";
        List<Float> successful=new ArrayList<>();
        ArrayList<Distance> distancesForInitStation = (ArrayList<Distance>) distanceRepository.findDistancesByStationName(station1);
        float distance = 0;
            for (int i = 0; i < distancesForInitStation.size(); i++) {
                station1 = stationFrom;
                if (distancesForInitStation.get(i).getStation1().getName().equals(station1)) {
                    station2 = distancesForInitStation.get(i).getStation2().getName();
                } else if (distancesForInitStation.get(i).getStation2().getName().equals(station1)) {
                    station2 = distancesForInitStation.get(i).getStation1().getName();
                }
                Distance firstDist = findDistanceBetweenNeighboring(stationRepository.getStationByName(station1).getId(), stationRepository.getStationByName(station2).getId());
                distance = firstDist.getDistance().floatValue();
                while (station2.compareTo(stationTo) != 0 && distanceRepository.findDistancesByStationName(station2).size()==2) {
                    Distance currentDistance = null;
                        currentDistance = distanceRepository.findNextDistance(station2, station1);
                        Pair<String, String> newStationPair = findNextStationPair(station1, station2, currentDistance);
                        station1 = newStationPair.getFirst();
                        station2 = newStationPair.getSecond();

                    //System.out.println(currentDistance);
                    distance += currentDistance.getDistance().floatValue();
                }
                if (station2.compareTo(stationTo) == 0) successful.add(distance);

            }
        if(station2.compareTo(stationTo)!=0)
        {
            distance=-1;
        }

        if(!successful.isEmpty()) distance = Collections.min(successful);
        return distance;
    }

    public Float getDistanceForTrain(String stationFrom, String stationTo, String trainName){
        ArrayList<Stop> stops = (ArrayList<Stop>) stopService.findStopsByTrain(trainService.getTrainByName(trainName).getId(),stationRepository.getStationByName(stationFrom).getId(),stationRepository.getStationByName(stationTo).getId());
        float distance= 0.0F;
        System.out.println(stops.size());
        for(int i=1;i<stops.size();i++){

                distance+=Dijkstra(stops.get(i-1).getStation().getName(),stops.get(i).getStation().getName()).getFirst();
                List<String> intermediate=Dijkstra(stops.get(i-1).getStation().getName(),stops.get(i).getStation().getName()).getSecond();
                for(int j=0;j<intermediate.size();j++){
                    System.out.println(intermediate.get(j));
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
//    public Float getDistanceForTrainTest(String stationFrom, String stationTo, String trainName){
//        List<Stop> stops = stopService.findStopsByTrain(trainService.getTrainByName(trainName).getId(),stationRepository.getStationByName(stationFrom).getId(),stationRepository.getStationByName(stationTo).getId());
//        float distance= 0.0F;
//        System.out.println(stops.size());
//        for(int i=1;i<stops.size();i++){
//            Float dist_tmp=findDistanceBetweenJunctionStations(stops.get(i-1).getStation().getName(),stops.get(i).getStation().getName());
//            String stationFromTmp=stops.get(i-1).getStation().getName();
//            String stationToTmp=stops.get(i).getStation().getName();
//            LinkedList<Station> station_from=new LinkedList<>(findNearestJunctionStations(stationFromTmp));
//            for(var s:station_from){
//              //  System.out.println(s);
//            }
//            LinkedList<Station> station_to=new LinkedList<>(findNearestJunctionStations(stationToTmp));
//            LinkedList<String> station_from_tmp=new LinkedList<>();
//            LinkedList<Station> nearestJunctionStations=new LinkedList<>();
//            LinkedList<Station> nearestJunctionStations_=new LinkedList<>();
//            int station_from_size=station_from.size();
//                for (int j = 0; j < station_from_size; j++) {
////
//                    nearestJunctionStations=new LinkedList<>(findNearestJunctionStations(station_from.get(j).getName()));
//                   nearestJunctionStations.removeAll(findNearestJunctionStations(Collections.singleton(stationFromTmp)));
//                    for (var s:nearestJunctionStations) {
//                        System.out.println(s+"-"+station_from.get(j)+" "+findDistanceBetweenJunctionStations(s.getName(),station_from.get(j).getName()));
//                    }
//                   // System.out.println();
//                    nearestJunctionStations_.addAll(nearestJunctionStations);
//                }
//                //stationFromTmp =
//
//
//
//            Set<Station> mergedSet = new LinkedHashSet<>(nearestJunctionStations_);
//            nearestJunctionStations_=new LinkedList<>(mergedSet);
//            for(var s:nearestJunctionStations_){
//             //   System.out.println(s);
//            }
//
//        }
//        return distance;
//    }
    public List<Distance> findDistancesByStationName(String name)
    {
        return distanceRepository.findDistancesByStationName(name);
    }
}
