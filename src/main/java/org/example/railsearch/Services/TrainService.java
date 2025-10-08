package org.example.railsearch.Services;

import jakarta.transaction.Transactional;
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
    private final DistanceRepository distanceRepository;
    //private final DistanceService distanceService;

    public TrainService(TrainRepository trainRepository, StationRepository stationRepository, StopRepository stopRepository, DistanceRepository distanceRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository=stationRepository;
        //this.distanceService = distanceService;
        this.stopRepository = stopRepository;
        this.distanceRepository = distanceRepository;
    }

    @Transactional
    public void printAll() {
        List<Train> trains = trainRepository.findAll();
        for (Train train : trains) {
            System.out.println(train.getName());
        }
    }
        @Transactional
        public List<Train> getTrainsByStations(int id1, int id2){
            return trainRepository.getTrainsByStations(id1,id2);
            }

        public Train getTrainByName(String name){
            return trainRepository.getTrainByName(name);
        }

private LocalTime getStopTime(Stop stop, boolean trueIfArrivalElseDeparture) {
    if (trueIfArrivalElseDeparture) {
        if (stop.getArrivalTime() != null) return stop.getArrivalTime();
        if (stop.getDepartureTime() != null) return stop.getDepartureTime();
    } else {
        if (stop.getDepartureTime() != null) return stop.getDepartureTime();
        if (stop.getArrivalTime() != null) return stop.getArrivalTime();
    }
    return null;
}
public record ConnectionStep(Station station, LocalTime time, Train train) {
        @Override
    public String toString() {
            return station.getName() + " " + time+" "+train.getName();
        }
}

    public record OnboardingTrain(Train train, Stop boardingStop, LocalTime time) {}

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

        PriorityQueue<OnboardingTrain> pq = new PriorityQueue<>(Comparator.comparing(t -> t.time));
        Map<Integer, LocalTime> bestTimeByTrainId = new HashMap<>();
        Map<Integer, OnboardingTrain> prevOnboardingTrainByTrainId = new HashMap<>();
        Map<Integer, OnboardingTrain> onboardingTrainByTrainId = new HashMap<>();

        Map<Integer, Stop> prevStopByDepartureStop = new HashMap<>();

        List<Stop> startStops = stopRepository.findDeparturesFromStationAfter(startStation.getId(), startTime);
int k=0,l=0;
        for (Stop s : startStops) {
            LocalTime dep = getStopTime(s, false);
            if (dep == null) {continue;}
            Train train = s.getTrain();
            System.out.println(train.getName());
            OnboardingTrain st = new OnboardingTrain(train, s, dep);
            LocalTime best = bestTimeByTrainId.get(train.getId());
            if(!isTrainOfOneOfTransporters(s.getTrain(),transporters)) continue;
                if (best == null || dep.isBefore(best)) {
//                if (best!=null && dep.isBefore(best))l++;

                    bestTimeByTrainId.put(train.getId(), dep);
                    pq.add(st);
                    prevOnboardingTrainByTrainId.put(train.getId(), null);
                    onboardingTrainByTrainId.put(train.getId(), st);

                }

        }
        System.out.println(l);
        if (pq.isEmpty()) return List.of();

        Stop foundArrivalStop = null;
        Train foundTrain = null;

        while (!pq.isEmpty()) {
            OnboardingTrain ts = pq.poll();
            Train train = ts.train();
            if(!isTrainOfOneOfTransporters(train,transporters)) continue;

            Stop boarding = ts.boardingStop();
            LocalTime boardTime = ts.time();

            LocalTime bestKnown = bestTimeByTrainId.get(train.getId());
            if (bestKnown != null && boardTime.isAfter(bestKnown)){
//                k++;
                continue;}

            List<Stop> stopsOfTrain = stopRepository.findStopsByTrainOrdered(train.getId());

            int idx = -1;
            for (int i = 0; i < stopsOfTrain.size(); i++) {
                if (stopsOfTrain.get(i).getId().equals(boarding.getId())) { idx = i; break; }
            }
            if (idx == -1) continue;

            for (int j = idx + 1; j < stopsOfTrain.size(); j++) {
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

            
            for (int j = idx + 1; j < stopsOfTrain.size(); j++) {
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
                        if (known != null && departureTime.isBefore(known)){
                            k++;
                        }

                        bestTimeByTrainId.put(nextTrain.getId(), departureTime);
                        OnboardingTrain next = new OnboardingTrain(nextTrain, departureStop, departureTime);
                        pq.add(next);
                        prevOnboardingTrainByTrainId.put(nextTrain.getId(), ts);
                        prevStopByDepartureStop.put(departureStop.getId(), stop2);
                        onboardingTrainByTrainId.put(nextTrain.getId(), next);

                    }
                }
            }
        }
        System.out.println(k);
        System.out.println("2-"+k);
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
            Stop depStop = onboardingTrain.boardingStop();
            LocalTime depTime = getStopTime(depStop, false);

            ConnectionSegment seg = new ConnectionSegment(
                    depStop.getStation(), depTime,
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

            Stop prevArrivalStop = prevStopByDepartureStop.get(depStop.getId());
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

            LocalTime firstDep = res.get(0).departure();
            if (firstDepartures.contains(firstDep)) {
                searchFrom = firstDep.plusMinutes(1);
                continue;
            }

            results.add(res);
            firstDepartures.add(firstDep);

            searchFrom = firstDep.plusSeconds(1);

            if (searchFrom.isAfter(endOfDay)) break;
        }

        return results;
    }

}




