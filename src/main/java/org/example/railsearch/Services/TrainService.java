package org.example.railsearch.Services;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Service
public class TrainService {
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final StopRepository stopRepository;

    public TrainService(TrainRepository trainRepository, StationRepository stationRepository, StopRepository stopRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository=stationRepository;
        this.stopRepository = stopRepository;
    }




        public Train getTrainByName(String name){
            return trainRepository.getTrainByName(name);
        }

private LocalTime getStopTime(Stop stop, boolean trueIfArrivalElseDeparture) {
    LocalTime true_ = trueIfArrivalElseDeparture ? stop.getArrivalTime() : stop.getDepartureTime();
    LocalTime else_ = trueIfArrivalElseDeparture ? stop.getDepartureTime() : stop.getArrivalTime();
    return true_ != null ? true_ : else_;
}


    public record OnboardingTrain(Train train, Stop onboardingStop, LocalTime time) {}

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
        Map<Integer, LocalTime> bestTimeByTrainId = new HashMap<>();
        Map<Integer, OnboardingTrain> prevOnboardingTrainByTrainId = new HashMap<>();
        Map<Integer, OnboardingTrain> onboardingTrainByTrainId = new HashMap<>();
        Map<OnboardingTrain,LocalTime> queue= new HashMap<>();
        Map<Integer, Stop> prevStopByDepartureStop = new HashMap<>();

        List<Stop> startStops = stopRepository.findDeparturesFromStationAfter(startStation.getId(), startTime);
        for (Stop s : startStops) {
            LocalTime departureTime = getStopTime(s, false);
            if (departureTime == null) {continue;}
            Train train = s.getTrain();
            System.out.println(train.getName());
            OnboardingTrain onboardingTrain = new OnboardingTrain(train, s, departureTime);
            LocalTime best = bestTimeByTrainId.get(train.getId());
            if(!isTrainOfOneOfTransporters(s.getTrain(),transporters)) continue;
            if (best == null || departureTime.isBefore(best)) {

                    bestTimeByTrainId.put(train.getId(), departureTime);
                    //pq.add(onboardingTrain);
                    queue.put( onboardingTrain,departureTime);
                    prevOnboardingTrainByTrainId.put(train.getId(), null);
                    onboardingTrainByTrainId.put(train.getId(), onboardingTrain);

                }

        }
        //if (pq.isEmpty()) return List.of();

        Stop foundArrivalStop = null;
        Train foundTrain = null;

        while (!queue.isEmpty()) {
            Map.Entry<OnboardingTrain,LocalTime> u = null;
            for (Map.Entry<OnboardingTrain,LocalTime> entry : queue.entrySet()) {
                if (u == null || entry.getValue().isBefore(u.getValue())) {
                    u= entry;
                }
            }
            assert u != null;
            queue.remove(u.getKey());
            var onboardingTrain=u.getKey();
            Train train = onboardingTrain.train();
            if(!isTrainOfOneOfTransporters(train,transporters)) continue;

            Stop onboardingStop = onboardingTrain.onboardingStop();
            LocalTime onboardingTime = onboardingTrain.time();

            LocalTime bestKnown = bestTimeByTrainId.get(train.getId());
            if (bestKnown != null && onboardingTime.isAfter(bestKnown)){
                continue;
            }

            List<Stop> stopsOfTrain = stopRepository.findStopsByTrainOrdered(train.getId());

            int onboardingStopIndex = -1;
            for (int i = 0; i < stopsOfTrain.size(); i++) {
                if (stopsOfTrain.get(i).getId().equals(onboardingStop.getId())) { onboardingStopIndex = i; break; }
            }
            if (onboardingStopIndex == -1) continue;

            for (int j = onboardingStopIndex + 1; j < stopsOfTrain.size(); j++) {
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

            
            for (int j = onboardingStopIndex + 1; j < stopsOfTrain.size(); j++) {
                Stop stop2 = stopsOfTrain.get(j);
                LocalTime arrival2 = getStopTime(stop2, true);
                if (arrival2 == null) continue;

                LocalTime earliestDeparture = arrival2.plus(minTimeTrainChange);
                List<Stop> consideredDepartures =
                        stopRepository.findDeparturesFromStationAfterLimited(stop2.getStation().getId(), earliestDeparture);

                for (Stop departureStop : consideredDepartures) {
                    if (departureStop.getTrain().getId().equals(train.getId())) continue;

                    LocalTime departureTime = getStopTime(departureStop, false);
                    if (departureTime == null) continue;

                    Train nextTrain = departureStop.getTrain();
                    if(!isTrainOfOneOfTransporters(nextTrain,transporters)) continue;

                    LocalTime known = bestTimeByTrainId.get(nextTrain.getId());

                    if (known == null || departureTime.isBefore(known)) {
                        bestTimeByTrainId.put(nextTrain.getId(), departureTime);
                        OnboardingTrain next = new OnboardingTrain(nextTrain, departureStop, departureTime);
                        //pq.add(next);
                        queue.put(next, departureTime);
                        prevOnboardingTrainByTrainId.put(nextTrain.getId(), onboardingTrain);
                        prevStopByDepartureStop.put(departureStop.getId(), stop2);
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
            OnboardingTrain onboardingTrain = onboardingTrainByTrainId.get(currentTrain.getId());
            if (onboardingTrain == null) {
                break;
            }
            Stop departureStop = onboardingTrain.onboardingStop();
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




