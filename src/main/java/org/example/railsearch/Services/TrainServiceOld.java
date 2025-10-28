package org.example.railsearch.Services;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.*;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Service
public class TrainServiceOld {
    @Autowired
    private TrainRepository trainRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private StopRepository stopRepository;






        public Train getTrainByName(String name){
            return trainRepository.getTrainByName(name);
        }

private LocalTime getStopTime(Stop stop, boolean trueIfArrivalElseDeparture) {
    LocalTime true_ = trueIfArrivalElseDeparture ? stop.getArrivalTime() : stop.getDepartureTime();
    LocalTime else_ = trueIfArrivalElseDeparture ? stop.getDepartureTime() : stop.getArrivalTime();
    return true_ != null ? true_ : else_;
}


    public record TrainWithStop(Train train, Stop stop) {}

    public record ConnectionSegment(
            Station fromStation,
            LocalTime departure,
            Station toStation,
            LocalTime arrival,
            Train train
    ) {
        @Override
        public String toString() {
            return fromStation.getName() + " " + departure +
                    " → " + toStation.getName() + " " + arrival +
                    " (" + train.getName() + ")";
        }
    }
    public boolean isTrainOfOneOfTransporters(Train t,List<String> transporters) {
        for (String transporter : transporters) {
            if (t.getName().split(" ")[0].equals(transporter)) {
                return true;
            }
        }
        return false;
    }
    public List<ConnectionSegment> findFastestConnection(
            String stationFrom,
            String stationTo,
            LocalTime startTime,
            List<String> transporters) {
        Station startStation = stationRepository.findStationByName(stationFrom);
        Station endStation   = stationRepository.findStationByName(stationTo);

        if (startStation == null || endStation == null) return List.of();

        final Duration minTimeTrainChange = Duration.ofMinutes(5);

        //PriorityQueue<OnboardingTrain> pq = new PriorityQueue<>(Comparator.comparing(onboardingTrain -> onboardingTrain.time));
        Map<Pair<Integer,Integer>, Long> bestTimeByTrainId = new HashMap<>();
        Map<Integer, TrainWithStop> prevTrainWithStopByTrainId = new HashMap<>();
        Map<Integer, TrainWithStop> trainWithStopByTrainId = new HashMap<>();
        Map<TrainWithStop, Long> queue= new HashMap<>();
        Map<Integer, Stop> prevStopByStop = new HashMap<>();
        Stop foundArrivalStop = null;
        Train foundTrain = null;
        LocalTime startTime2 = startTime;
        Station fromSt = stationRepository.findStationByName(stationFrom);
        Station destSt=stationRepository.findStationByName(stationTo);

        List<Stop> startStops = stopRepository.findDeparturesFromStationAfter(startStation.getId(), startTime);
        for (Stop s : startStops) {
            LocalTime departureTime = getStopTime(s, false);
            if (departureTime == null) {continue;}
            Train train = s.getTrain();

//            System.out.println("trainId"+train.getId());
//
//            System.out.println("trainName "+train.getName());
            TrainWithStop trainWithStop=new TrainWithStop(train, s);
            Long best = bestTimeByTrainId.get(train.getId());
            if(!isTrainOfOneOfTransporters(s.getTrain(),transporters)) continue;
            Long timeBetween = Duration.between(startTime, departureTime).toMinutes();
            if (best==null ||timeBetween<best ) {
                    bestTimeByTrainId.put(new Pair<>(s.getStation().getId(),train.getId()), timeBetween);
                    //pq.add(onboardingTrain);
                    queue.put( trainWithStop,timeBetween);
                    prevTrainWithStopByTrainId.put(train.getId(), null);
                    trainWithStopByTrainId.put(train.getId(), trainWithStop);
                    prevStopByStop.put(s.getId(), trainWithStop.stop());
                }

        }
        System.out.println("Queue size: "+queue.size());
        if (queue.isEmpty()) return List.of();
//        for (Map.Entry<Integer,Long> entry : bestTimeByTrainId.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }

        while (!queue.isEmpty()) {
            System.out.println("qsize "+queue.size());
            Map.Entry<TrainWithStop,Long> u = null;
            for (Map.Entry<TrainWithStop,Long> entry : queue.entrySet()) {
                if (u == null || entry.getValue()<(u.getValue())) {
                    u= entry;
                }
            }

            queue.remove(u.getKey());

            var trainWithStop=u.getKey();
            Train train = trainWithStop.train();
            if(!isTrainOfOneOfTransporters(train,transporters)) continue;

            Stop stop = trainWithStop.stop();
            LocalTime time = getStopTime(stop,true);
            Long bestKnown = bestTimeByTrainId.get(new Pair<>(stop.getStation().getId(),train.getId()));
            if (bestKnown != null && bestKnown<Duration.between(startTime2,time).toMinutes()){
                continue;
            }

            List<Stop> stopsOfTrain = stopRepository.findStopsByTrainOrdered(train.getId());

            int stopIndex = -1;
            for (int i = 0; i < stopsOfTrain.size(); i++) {
                if (stopsOfTrain.get(i).getId().equals(stop.getId())) { stopIndex = i; break; }
            }
            if (stopIndex == -1) continue;
//            LocalTime timeArriv=getStopTime(stopsOfTrain.get(onboardingStopIndex),true);
//            if(timeArriv!=null) startTime2=timeArriv;
            for (int j = stopIndex + 1; j < stopsOfTrain.size(); j++) {
                Stop nextStop = stopsOfTrain.get(j);
                LocalTime arrivalAtNext = getStopTime(nextStop, true);
                if (arrivalAtNext == null) continue;

                if (nextStop.getStation().getId().equals(endStation.getId())) {
                    foundArrivalStop = nextStop;
                    foundTrain = train;
                    break;


				}


            }
            if (foundArrivalStop != null) break;
            System.out.println("--------");
            
            for (int j = stopIndex + 1; j < stopsOfTrain.size(); j++) {
                Stop stop2 = stopsOfTrain.get(j);
                LocalTime arrival2 = getStopTime(stop2, true);
                if (arrival2 == null) continue;

                LocalTime earliestDeparture = arrival2.plus(minTimeTrainChange);
                List<Stop> consideredDepartures =
                        stopRepository.findDeparturesFromStationAfter(stop2.getStation().getId(), earliestDeparture);

                for (Stop departureStop : consideredDepartures) {
                    if (departureStop.getTrain().getId().equals(train.getId())) continue;

                    LocalTime departureTime = getStopTime(departureStop, false);
                    if (departureTime == null) continue;

                    Train nextTrain = departureStop.getTrain();

                    if(!isTrainOfOneOfTransporters(nextTrain,transporters)) continue;

                    Long known = bestTimeByTrainId.get(new Pair<>(departureStop.getStation().getId(),nextTrain.getId()));
                    var timeBetween = Duration.between(startTime2,departureTime).toMinutes();
                    if ((known==null || timeBetween<known)&& stop2.getId()<departureStop.getId()) {
                        bestTimeByTrainId.put(new Pair<>(departureStop.getStation().getId(),nextTrain.getId()), timeBetween);
                        TrainWithStop next = new TrainWithStop(nextTrain, departureStop);
                        //pq.add(next);

                        queue.put(next, timeBetween);
                        prevTrainWithStopByTrainId.put(nextTrain.getId(), trainWithStop);
                        prevStopByStop.put(departureStop.getId(), stop2);
                        trainWithStopByTrainId.put(nextTrain.getId(), next);

                    }
                }
            }
        }
        if (foundArrivalStop == null) {
            return List.of();
        }

        List<ConnectionSegment> path = new ArrayList<>();
        System.out.println("found: "+foundTrain.getName());
        Train currentTrain = foundTrain;
        Stop arrivalStop = foundArrivalStop;
        LocalTime arrivalTime = getStopTime(arrivalStop, true);

        while (currentTrain != null) {
            System.out.println("curr: "+currentTrain.getName());
            TrainWithStop trainWithStop= trainWithStopByTrainId.get(currentTrain.getId());
            if (trainWithStop == null) {
                break;
            }
            Stop departureStop = trainWithStop.stop();
            LocalTime depTime = getStopTime(departureStop, false);

            ConnectionSegment seg = new ConnectionSegment(
                    departureStop.getStation(), depTime,
                    arrivalStop.getStation(), arrivalTime,
                    currentTrain
            );

            if (!seg.fromStation().getId().equals(seg.toStation().getId())) {
                path.add(seg);
            }

            TrainWithStop trainWithStopPrev = prevTrainWithStopByTrainId.get(currentTrain.getId());
            if (trainWithStopPrev == null) {
                break;
            }

            Stop prevArrivalStop = prevStopByStop.get(departureStop.getId());
            if (prevArrivalStop == null) {
                break;
            }
            arrivalStop = prevArrivalStop;
            arrivalTime = getStopTime(arrivalStop, true);
            currentTrain = trainWithStopPrev.train();
        }

        Collections.reverse(path);
        return path;
    }

    public List<List<ConnectionSegment>> findAllConnectionsForDay(
            String stationFrom,
            String stationTo,
            LocalTime startTime,
            List<String> transporters) {
        List<List<ConnectionSegment>> results = new ArrayList<>();
        LocalTime endOfDay = LocalTime.of(23, 59, 59);
        LocalTime searchFrom = startTime;


        Set<LocalTime> firstDepartures = new HashSet<>();

        while (!searchFrom.isAfter(endOfDay)) {
            List<ConnectionSegment> res = findFastestConnection(stationFrom, stationTo, searchFrom,transporters);
            if (res == null || res.isEmpty()) break;

            LocalTime firstDeparture = res.get(0).departure();
            if (firstDepartures.contains(firstDeparture)) {
                searchFrom = firstDeparture.plusMinutes(1);
                continue;
            }

            results.add(res);
            firstDepartures.add(firstDeparture);

            searchFrom = firstDeparture.plusMinutes(1);

            if (searchFrom.isAfter(endOfDay)) break;
        }

        return results;
    }

}




