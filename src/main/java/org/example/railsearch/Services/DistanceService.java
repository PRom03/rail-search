package org.example.railsearch.Services;

import jakarta.transaction.Transactional;
import org.example.railsearch.Entities.Distance;
import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Repositories.DistanceRepository;
import org.example.railsearch.Repositories.StationRepository;
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


    public DistanceService(DistanceRepository distanceRepository, StationRepository stationRepository, StopService stopService, TrainService trainService) {
        this.distanceRepository = distanceRepository;
        this.stationRepository = stationRepository;
        this.stopService = stopService;
        this.trainService = trainService;
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
    public Float Dijkstra(String stationFrom,String stationTo) {
        List<Station> graph=stationRepository.findJunctionStations();
        System.out.println(graph.size());
        PriorityQueue<HashMap<Station,Float>> pq = new PriorityQueue<>((a,b)-> {
            Float valA = a.values().iterator().next();
            Float valB = b.values().iterator().next();
            return Float.compare(valA, valB);
        });
        Station prev=null;
        HashMap<Station,Float> hm=new HashMap<Station, Float>();
        hm.put(stationRepository.findStationByName(stationFrom),0.0F);
        pq.add(hm);
        HashMap<Station,Float> dist=new HashMap<>();
        dist.put(stationRepository.findStationByName(stationFrom),0.0F);

        for (var v :graph) {
            if (v.getName().compareTo(stationFrom)!=0) {
                prev = null;
                dist.put(v, 1500.0F);
                HashMap<Station,Float> hm_=new HashMap<Station, Float>();
                hm_.put(v,1500.0F);// Unknown distance from source to v
                pq.add(hm_);

            }
        }
//        var it=dist.entrySet().iterator();
//        for (; it.hasNext(); ) {
//            Map.Entry<Station,Float> kv=(Map.Entry<Station,Float>) it.next();
//            System.out.println(kv.getKey().getName()+" "+kv.getValue());
//
//
//        }
Float found=null;
            while (!pq.isEmpty()||found==null) {               // The main loop
                HashMap<Station,Float> u=pq.poll();
                assert u != null;
                var curr=u.entrySet().iterator().next().getKey();
                List<Station> neighbors=findNearestJunctionStations(curr.getName()).stream().distinct().toList();
                for(var neighbor:neighbors) {
                    System.out.println(neighbor.getName()+" "+dist.get(neighbor));
                }
//                var it_=u.keySet().iterator();
//                while (it_.hasNext()) {
//                    System.out.println(it_.next().getName());
//                }
                for(var neighbor:neighbors) {
                   // System.out.println(neighbor.getName());
                    Float dstnc=findDistanceBetweenJunctionStations(neighbor.getName(),curr.getName());
                    float alt=0;
                    alt = dist.get(curr) +dstnc;
                    if(dist.get(neighbor)==null) continue;
                    if (alt < dist.get(neighbor)) {
                        prev = u.keySet().iterator().next();
                        dist.put(neighbor, alt);
                        pq.removeIf(entry -> entry.containsKey(neighbor));
                        HashMap<Station, Float> hm_ = new HashMap<Station, Float>();
                        hm_.put(neighbor,alt);
                        pq.add(hm_);
                        if(neighbor.getName().equals(stationTo)) {
                            found=dist.get(neighbor);
                            break;
                        }
                    }
                    if(found!=null) break;
                }
                if(found!=null) break;
            }

         //   return (dist,prev)
        return found;
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
            Float dist_tmp=findDistanceBetweenJunctionStations(stops.get(i-1).getStation().getName(),stops.get(i).getStation().getName());
            String stationFromTmp=stops.get(i-1).getStation().getName();
            String stationToTmp=stops.get(i).getStation().getName();
            if(dist_tmp!=-1) distance+=dist_tmp;
            else{
                distance+=Dijkstra(stationFromTmp,stationToTmp);
            }
        }
        return distance;
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
