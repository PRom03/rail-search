package org.example.railsearch.Services;

import jakarta.transaction.Transactional;
import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Stop;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Repositories.*;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;
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
//        public List<Train> getTrains(String stationFrom, String stationTo){
//
//        }
//public record ConnectionStep(Station station, LocalTime arrival, Train train) {
//    @Override
//    public String toString() {
//        return station.getName() + " " + arrival + " " + train.getName()+"\n";
//    }

  
//    public Pair<Duration, List<ConnectionStep>> findFastestConnection(
//            String stationFrom,
//            String stationTo,
//            LocalTime startTime
//    ) {
//        Station startStation = stationRepository.findStationByName(stationFrom);
//        Station endStation   = stationRepository.findStationByName(stationTo);
//
//        // --- 1. sprawdzenie bezpośrednich połączeń ---
//        List<Stop> direct = stopRepository.findDirectConnections(
//                startStation.getId(),
//                endStation.getId(),
//                startTime
//        );
//
//        if (!direct.isEmpty()) {
//            Stop arrival = direct.get(0); // najwcześniejszy przyjazd
//            Duration totalTime = Duration.between(startTime, arrival.getArrivalTime());
//            List<ConnectionStep> path = List.of(
//                    new ConnectionStep(startStation, startTime, arrival.getTrain()),
//                    new ConnectionStep(endStation, arrival.getArrivalTime(), arrival.getTrain())
//            );
//            return Pair.of(totalTime, path);
//        }
//
//        // --- 2. jeśli nie ma bezpośrednich, szukamy przez przesiadki ---
//        Duration minTransfer = Duration.ofMinutes(5);
//
//        Map<Stop, LocalTime> arrivalTime = new HashMap<>();
//        Map<Stop, Stop> prevStop = new HashMap<>();
//        Set<Stop> visited = new HashSet<>();
//
//        PriorityQueue<Stop> pq = new PriorityQueue<>(
//                Comparator.comparing(s -> arrivalTime.getOrDefault(s, LocalTime.MAX))
//        );
//
//        // startowe odjazdy
//        List<Stop> startStops = stopRepository.findDeparturesFromStationAfter(startStation.getId(), startTime);
//        for (Stop s : startStops) {
//            if (s.getDepartureTime() != null) {
//                arrivalTime.put(s, s.getDepartureTime());
//                pq.add(s);
//            }
//        }
//
//        Stop target = null;
//
//        while (!pq.isEmpty()) {
//            Stop curr = pq.poll();
//            if (!visited.add(curr)) continue;
//
//            LocalTime currArrival = arrivalTime.get(curr);
//            if (currArrival == null) continue;
//
//            if (curr.getStation().equals(endStation)) {
//                target = curr;
//                break;
//            }
//
//            // 1. Kontynuacja tym samym pociągiem
//            for (Stop ns : stopRepository.findNextStopsInSameTrain(curr.getId())) {
//                LocalTime candArrival = ns.getArrivalTime() != null ? ns.getArrivalTime() : ns.getDepartureTime();
//                if (candArrival != null && candArrival.isBefore(arrivalTime.getOrDefault(ns, LocalTime.MAX))) {
//                    arrivalTime.put(ns, candArrival);
//                    prevStop.put(ns, curr);
//                    pq.add(ns);
//                }
//            }
//
//            // 2. Najlepsza przesiadka na tej samej stacji
//            Stop bestTransfer = stopRepository.findTransfersAtSameStation(curr.getId()).stream()
//                    .filter(s2 -> curr.getArrivalTime() != null
//                            && s2.getDepartureTime() != null
//                            && !s2.getDepartureTime().isBefore(curr.getArrivalTime().plus(minTransfer)))
//                    .min(Comparator.comparing(Stop::getDepartureTime))
//                    .orElse(null);
//
//            if (bestTransfer != null) {
//                LocalTime candArrival = bestTransfer.getArrivalTime() != null
//                        ? bestTransfer.getArrivalTime()
//                        : bestTransfer.getDepartureTime();
//
//                if (candArrival != null && candArrival.isBefore(arrivalTime.getOrDefault(bestTransfer, LocalTime.MAX))) {
//                    arrivalTime.put(bestTransfer, candArrival);
//                    prevStop.put(bestTransfer, curr);
//                    pq.add(bestTransfer);
//                }
//            }
//        }
//
//        if (target == null) {
//            return Pair.of(null, List.of()); // brak połączenia
//        }
//
//        // rekonstrukcja ścieżki
//        List<ConnectionStep> path = new ArrayList<>();
//        for (Stop at = target; at != null; at = prevStop.get(at)) {
//            path.add(new ConnectionStep(at.getStation(), arrivalTime.get(at), at.getTrain()));
//        }
//        Collections.reverse(path);
//
//        Duration totalTime = Duration.between(startTime, arrivalTime.get(target));
//        return Pair.of(totalTime, path);
//    }
//
private LocalTime effectiveTime(Stop stop, boolean preferArrival) {
    if (preferArrival) {
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

    public record TrainState(Train train, Stop boardingStop, LocalTime time) {}

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

    public Pair<Duration, List<ConnectionSegment>> findFastestConnection(
            String stationFrom,
            String stationTo,
            LocalTime startTime
    ) {
        Station startStation = stationRepository.findStationByName(stationFrom);
        Station endStation   = stationRepository.findStationByName(stationTo);
        if (startStation == null || endStation == null) return Pair.of(null, List.of());

        final Duration minTransfer = Duration.ofMinutes(5);
        final int TRANSFER_LIMIT = 10; // ogranicz liczbę rozważanych odjazdów przy transferze

        PriorityQueue<TrainState> pq = new PriorityQueue<>(Comparator.comparing(ts -> ts.time));
        Map<Integer, LocalTime> bestTimeByTrain = new HashMap<>();
        // mapuje trainId -> TrainState poprzedniego pociągu (jak w Twoim kodzie)
        Map<Integer, TrainState> prevStateByTrain = new HashMap<>();
        // dodatkowo: mapuje trainId -> TrainState, który reprezentuje boarding dla tego pociągu
        Map<Integer, TrainState> boardingStateByTrain = new HashMap<>();
        // pomoc: map stopId -> previous stop id (depStopId -> sMid.getId())
        Map<Integer, Integer> prevStopId = new HashMap<>();
        // oraz depStopId -> poprzedni Stop (żeby pobrać czas przyjazdu)
        Map<Integer, Stop> prevStopByDepStop = new HashMap<>();

        // --- start: wszystkie pociągi odjeżdżające ze stacji startowej po startTime ---
        List<Stop> startStops = stopRepository.findDeparturesFromStationAfter(startStation.getId(), startTime);
        for (Stop s : startStops) {
            LocalTime dep = effectiveTime(s, false); // prefer departure
            if (dep == null) continue;
            Train train = s.getTrain();
            TrainState st = new TrainState(train, s, dep);
            LocalTime best = bestTimeByTrain.get(train.getId());
            if (best == null || dep.isBefore(best)) {
                bestTimeByTrain.put(train.getId(), dep);
                pq.add(st);
                prevStateByTrain.put(train.getId(), null); // boarding from start
                prevStopId.put(s.getId(), null);
                boardingStateByTrain.put(train.getId(), st); // zapisz boarding dla tego pociągu
            }
        }

        if (pq.isEmpty()) return Pair.of(null, List.of());

        Stop foundArrivalStop = null;
        Train foundTrain = null;
        LocalTime foundArrivalTime = null;

        while (!pq.isEmpty()) {
            TrainState ts = pq.poll();
            Train train = ts.train();
            Stop boarding = ts.boardingStop();
            LocalTime boardTime = ts.time();

            LocalTime bestKnown = bestTimeByTrain.get(train.getId());
            if (bestKnown != null && boardTime.isAfter(bestKnown)) continue;

            List<Stop> stopsOfTrain = stopRepository.findStopsByTrainOrdered(train.getId());

            int idx = -1;
            for (int i = 0; i < stopsOfTrain.size(); i++) {
                if (stopsOfTrain.get(i).getId().equals(boarding.getId())) { idx = i; break; }
            }
            if (idx == -1) continue;

            // 1) sprawdź czy tym pociągiem dojedziemy do stacji docelowej
            for (int j = idx + 1; j < stopsOfTrain.size(); j++) {
                Stop sNext = stopsOfTrain.get(j);
                LocalTime arrivalAtNext = effectiveTime(sNext, true); // prefer arrival
                if (arrivalAtNext == null) continue;

                if (sNext.getStation().getId().equals(endStation.getId())) {
                    foundArrivalStop = sNext;
                    foundTrain = train;
                    foundArrivalTime = arrivalAtNext;
                    break;
                }
            }
            if (foundArrivalStop != null) break;

            // 2) przesiadki
            for (int j = idx + 1; j < stopsOfTrain.size(); j++) {
                Stop sMid = stopsOfTrain.get(j);
                LocalTime arrivalAtMid = effectiveTime(sMid, true);
                if (arrivalAtMid == null) continue;

                LocalTime earliestDep = arrivalAtMid.plus(minTransfer);
                List<Stop> candidateDepartures =
                        stopRepository.findDeparturesFromStationAfterLimited(sMid.getStation().getId(), earliestDep);

                for (Stop depStop : candidateDepartures) {
                    if (depStop.getTrain().getId().equals(train.getId())) continue;

                    LocalTime depTime = effectiveTime(depStop, false);
                    if (depTime == null) continue;

                    Train nextTrain = depStop.getTrain();
                    LocalTime known = bestTimeByTrain.get(nextTrain.getId());
                    if (known == null || depTime.isBefore(known)) {
                        bestTimeByTrain.put(nextTrain.getId(), depTime);
                        TrainState nextState = new TrainState(nextTrain, depStop, depTime);
                        pq.add(nextState);
                        // zapisz relacje do rekonstrukcji
                        prevStateByTrain.put(nextTrain.getId(), ts); // poprzedni TrainState (ten, z którego przesiadamy)
                        prevStopId.put(depStop.getId(), sMid.getId()); // depStopId -> sMidId
                        prevStopByDepStop.put(depStop.getId(), sMid); // depStopId -> sMid (obiekt Stop)
                        boardingStateByTrain.put(nextTrain.getId(), nextState); // boarding dla nextTrain
                    }
                }
            }
        } // koniec pętli PQ

        if (foundArrivalStop == null) {
            return Pair.of(null, List.of());
        }

        // --- REKONSTRUKCJA JAKO ODCINKI ---
        List<ConnectionSegment> path = new ArrayList<>();

        Train currentTrain = foundTrain;
        Stop arrivalStop = foundArrivalStop;
        LocalTime arrivalTime = effectiveTime(arrivalStop, true);

        while (currentTrain != null) {
            TrainState boardingState = boardingStateByTrain.get(currentTrain.getId());
            if (boardingState == null) {
                // nieoczekiwana sytuacja, przerwij bez dalszych segmentów
                break;
            }
            Stop depStop = boardingState.boardingStop();
            LocalTime depTime = effectiveTime(depStop, false);

            // utwórz segment: depStop.station@depTime -> arrivalStop.station@arrivalTime
            ConnectionSegment seg = new ConnectionSegment(
                    depStop.getStation(), depTime,
                    arrivalStop.getStation(), arrivalTime,
                    currentTrain
            );

            // filtr: pomiń segmenty, które zaczynają i kończą się na tej samej stacji
            if (!seg.fromStation().getId().equals(seg.toStation().getId())) {
                path.add(seg);
            }

            // id poprzedniego pociągu (jeśli był)
            TrainState prevTrainState = prevStateByTrain.get(currentTrain.getId());
            if (prevTrainState == null) {
                // dojście do startu — koniec rekonstrukcji
                break;
            }

            // przygotuj wartości dla następnej iteracji (cofamy się)
            // poprzedni arrivalStop to stop, z którego przesiadaliśmy: prevStopByDepStop.get(depStop.getId())
            Stop prevArrivalStop = prevStopByDepStop.get(depStop.getId());
            if (prevArrivalStop == null) {
                // brak danych — zakończ
                break;
            }
            arrivalStop = prevArrivalStop;
            arrivalTime = effectiveTime(arrivalStop, true);
            currentTrain = prevTrainState.train();
        }

        Collections.reverse(path);
        Duration total = Duration.between(startTime, foundArrivalTime);
        return Pair.of(total, path);
    }



    public void getConns(String stationFrom,String stationTo,LocalTime startTime){
            List<ConnectionSegment> list = findFastestConnection(stationFrom, stationTo, startTime).getSecond();
            for (ConnectionSegment cs : list) {
                System.out.println(cs.toString());
            }
        }
}




