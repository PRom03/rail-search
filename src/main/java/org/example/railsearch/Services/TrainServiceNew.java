package org.example.railsearch.Services;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.StationRepository;
import org.example.railsearch.Repositories.StopRepository;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainServiceNew {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private StopRepository stopRepository;







    private LocalTime getStopTime(Stop stop, boolean trueIfArrivalElseDeparture) {
        LocalTime true_ = trueIfArrivalElseDeparture ? stop.getArrivalTime() : stop.getDepartureTime();
        LocalTime else_ = trueIfArrivalElseDeparture ? stop.getDepartureTime() : stop.getArrivalTime();
        return true_ != null ? true_ : else_;
    }


    public record OnboardingTrain(Train train, Station station) {}

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
    public boolean isNotTrainOfOneOfTransporters(Train t,List<String> transporters) {
        for (String transporter : transporters) {
            if (t.getName().split(" ")[0].equals(transporter)) {
                return false;
            }
        }
        return true;
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

        PriorityQueue<Pair<Station,Pair<Stop,Long>>> queue = new PriorityQueue<>(Comparator.comparing(q -> q.getValue1().getValue1()));
        Map<Integer, Long> bestTimeByStationId = new HashMap<>();
        Map<Integer, OnboardingTrain> prevOnboardingTrainByTrainId = new HashMap<>();
        Map<Integer, OnboardingTrain> onboardingTrainByTrainId = new HashMap<>();
        Map<OnboardingTrain,Stop> onboardingStopByTrain = new HashMap<>();
        //Map<OnboardingTrain, Pair<Stop,Long>> queue= new HashMap<>();
        Map<Integer, Stop> prevStopByDepartureStop = new HashMap<>();
        Stop foundArrivalStop = null;
        Train foundTrain = null;

        List<Stop> startStops = stopRepository.findDeparturesFromStationAfter(startStation.getId(), startTime);
        for (Stop s : startStops) {
            LocalTime departureTime = getStopTime(s, false);
            if (departureTime == null) {continue;}
            Train train = s.getTrain();

//            System.out.println("trainId"+train.getId());
//
//            System.out.println("trainName "+train.getName());
            OnboardingTrain onboardingTrain = new OnboardingTrain(train, s.getStation());
            Long best = bestTimeByStationId.get(s.getStation().getId());
            if(isNotTrainOfOneOfTransporters(s.getTrain(), transporters)) continue;
            Long timeBetween = Duration.between(startTime, departureTime).toMinutes();
            if (best==null ||timeBetween<best ) {
                bestTimeByStationId.put(s.getStation().getId(), timeBetween);
                queue.add(new Pair<>(s.getStation(),new Pair<>(s,timeBetween)));
                prevOnboardingTrainByTrainId.put(train.getId(), null);
                onboardingStopByTrain.put(onboardingTrain,s);
                onboardingTrainByTrainId.put(train.getId(), onboardingTrain);
                prevStopByDepartureStop.put(s.getId(), s);
            }

        }
        System.out.println("Queue size: "+queue.size());
        if (queue.isEmpty()) return List.of();
//        for (Map.Entry<Integer,Long> entry : bestTimeByTrainId.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }

        while (!queue.isEmpty()) {
            System.out.println("qsize "+queue.size());
            var u=queue.poll();
            Train train = u.getValue1().getValue0().getTrain();
            if(isNotTrainOfOneOfTransporters(train, transporters)) continue;

            Stop onboardingStop = u.getValue1().getValue0();


            if (onboardingStop.getStation().getId().equals(endStation.getId())) {
                foundArrivalStop = onboardingStop;
                foundTrain = train;
                break;
            }
            Long onboardingTime = u.getValue1().getValue1();
            Long bestKnown = bestTimeByStationId.get(onboardingStop.getStation().getId());
            if (bestKnown != null && bestKnown<onboardingTime){
                continue;
            }

            List<Stop> stopsOfTrain = stopRepository.findStopsByTrainOrdered(train.getId());

            int onboardingStopIndex = -1;
            for (int i = 0; i < stopsOfTrain.size(); i++) {
                if (stopsOfTrain.get(i).getId().equals(onboardingStop.getId())) { onboardingStopIndex = i; break; }
            }
            if (onboardingStopIndex == -1) continue;
//            LocalTime timeArriv=getStopTime(stopsOfTrain.get(onboardingStopIndex),true);
//            if(timeArriv!=null) startTime2=timeArriv;

            //if (foundArrivalStop != null) break;
            System.out.println("--------");

            for (int j = onboardingStopIndex + 1; j < stopsOfTrain.size(); j++) {
                Stop stop2 = stopsOfTrain.get(j);
                LocalTime arrival2 = getStopTime(stop2, true);
                if (arrival2 == null) continue;
                System.out.println("station: "+stop2.getStation().getName()+", train: "+stop2.getTrain().getName());
//                List<Station> stopsOfTrain2=stopsOfTrain.subList(0,j+1).stream().map(Stop::getStation).toList();
//                stationsVisited.addAll(stopsOfTrain2);
                LocalTime earliestDeparture = arrival2.plus(minTimeTrainChange);
                List<Stop> consideredDepartures =
                        stopRepository.findDeparturesFromStationAfter(stop2.getStation().getId(), earliestDeparture);

                for (Stop departureStop : consideredDepartures) {

                    if (departureStop.getTrain().getId().equals(train.getId())) continue;

                    LocalTime departureTime = getStopTime(departureStop, false);
                    if (departureTime == null) continue;

                    Train nextTrain = departureStop.getTrain();


                    if(isNotTrainOfOneOfTransporters(nextTrain, transporters)) continue;
                    Long known = bestTimeByStationId.get(departureStop.getStation().getId());
                    var timeBetween = Duration.between(startTime,departureTime).toMinutes();

                    if ((known==null || timeBetween<known)/*&& stop2.getId()<departureStop.getId()*/) {
                        bestTimeByStationId.put(departureStop.getStation().getId(), timeBetween);
                        OnboardingTrain next = new OnboardingTrain(nextTrain, departureStop.getStation());
                        queue.add(new Pair<>(departureStop.getStation(), new Pair<>(departureStop,timeBetween)));
                        prevOnboardingTrainByTrainId.put(nextTrain.getId(), new OnboardingTrain(train, onboardingStop.getStation()));
                        prevStopByDepartureStop.put(departureStop.getId(), stop2);
                        onboardingStopByTrain.put(next,departureStop);

                        onboardingTrainByTrainId.put(nextTrain.getId(), next);

                    }
                }
            }
        }
        if (foundArrivalStop == null) {
            return List.of();
        }

        List<ConnectionSegment> path = new ArrayList<>();

        Train currentTrain = foundTrain;
        Stop arrivalStop = foundArrivalStop;
        LocalTime arrivalTime = getStopTime(arrivalStop, true);

        while (currentTrain != null) {
            System.out.println("curr: "+currentTrain.getName());
            OnboardingTrain onboardingTrain = onboardingTrainByTrainId.get(currentTrain.getId());
            if (onboardingTrain == null) {
                break;
            }
            Stop departureStop = onboardingStopByTrain.get(onboardingTrain);
            LocalTime depTime = getStopTime(departureStop, false);

            ConnectionSegment seg = new ConnectionSegment(
                    departureStop.getStation(), depTime,
                    arrivalStop.getStation(), arrivalTime,
                    currentTrain
            );

            if (!seg.fromStation().getId().equals(seg.toStation().getId())) {
                path.add(seg);
            }

            OnboardingTrain onboardingTrainPrev = prevOnboardingTrainByTrainId.get(currentTrain.getId());
            if (onboardingTrainPrev == null) {
                break;
            }

            Stop prevArrivalStop = prevStopByDepartureStop.get(departureStop.getId());
            if (prevArrivalStop == null) {
                break;
            }
            arrivalStop = prevArrivalStop;
            arrivalTime = getStopTime(arrivalStop, true);
            currentTrain = onboardingTrainPrev.train();
        }

        Collections.reverse(path);
        return path;
    }

    public List<List<ConnectionSegment>> findAllConnectionsForDay(
            String stationFrom,
            String stationTo,
            LocalTime startTime,
            List<String> transporters) {
        ArrayList<List<ConnectionSegment>> results = new ArrayList<>();
        LocalTime endOfDay = LocalTime.of(23, 59, 59);
        LocalTime searchFrom = startTime;


        Set<LocalTime> firstDepartures = new HashSet<>();

        while (!searchFrom.isAfter(endOfDay)) {
            List<ConnectionSegment> res = new ArrayList<>(findFastestConnection(stationFrom, stationTo, searchFrom,transporters));
            if (res.isEmpty()) break;

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




