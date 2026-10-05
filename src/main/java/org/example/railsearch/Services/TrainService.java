package org.example.railsearch.Services;


import org.example.railsearch.Entities.*;
import org.example.railsearch.Repositories.*;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TrainService {
    @Autowired
    private TrainRepository trainRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private TrainDateRepository trainDateRepository;
    @Autowired
    private TransporterRepository transporterRepository;


    public Train getTrainByName(String name){
        return trainRepository.getTrainByName(name);
    }

    private LocalTime getStopTime(Stop stop, boolean trueIfArrivalElseDeparture) {
        LocalTime true_ = trueIfArrivalElseDeparture ? stop.getArrivalTime() : stop.getDepartureTime();
        LocalTime else_ = trueIfArrivalElseDeparture ? stop.getDepartureTime() : stop.getArrivalTime();
        return true_ != null ? true_ : else_;
    }
    public List<Train> findAll() {
        return trainRepository.findAll();
    }
    public Train createTrain(String name) {
        Train train = new Train();
        train.setName(name);
        return trainRepository.save(train);
    }
    public Train updateTrain(long id,String name) {
        Train train=trainRepository.findById(id).orElseThrow()  ;
        Train existing =trainRepository.getTrainByName(name);
        if(existing==null)train.setName(name);
        else throw new RuntimeException("Pociąg o takiej nazwie już istnieje.");
        return trainRepository.save(train);
    }
    public Train findById(long id) {
        return trainRepository.findById(id).orElseThrow();
    }
    public void deleteById(long id) {
        Train train = trainRepository.findById(id).orElseThrow();
        List<Stop> stops=stopRepository.findStopsByTrain(train.getId());
        List<TrainDate> trainDates=trainDateRepository.findByTrain(train);
        stopRepository.deleteAll(stops);
        trainDateRepository.deleteAll(trainDates);
        trainRepository.delete(train);
    }

    public record TrainStop(Train train, Stop stop, LocalTime time) {

    }

    public record ConnectionSegment(
            Stop stopFrom,
            LocalTime departure,
            Stop stopTo,
            LocalTime arrival,
            Train train
    ) {
        @Override

        public String toString() {
            return stopFrom.getStation().getName() + " " + departure +
                    " → " + stopTo.getStation().getName() + " " + arrival +
                    " (" + train.getName() + ")";
        }
    }
    public boolean isTrainOfOneOfTransporters(Train t,List<String> transporters) {
        for (String transporter : transporters) {
            if(Objects.equals(transporter, "KM OsP")){
                if(t.getName().matches("KM [0-9]{5} [A-ZĄĆĘŁÓŚŹŻ]+")){
                    return true;
                }
            }
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
        PriorityQueue<Pair<TrainStop, Long>> queue = new PriorityQueue<>(Comparator.comparingLong(Pair::getValue1));
        int reqs=0;
        Map<TrainStop, TrainStop> prevTrainStopByTrainStop = new HashMap<>();
        Map<Pair<Integer,Integer>, Long> bestTimeByStationIdAndTrainId = new HashMap<>();
        List<Stop> startStops=stopRepository.findDeparturesFromStationAfter(stationRepository.findStationByName(stationFrom).getId(), startTime);
        reqs++;
        for (Stop s : startStops) {

            LocalTime departureTime = getStopTime(s, false);
            if (departureTime == null) continue;

            Train train = s.getTrain();
            if (!isTrainOfOneOfTransporters(train, transporters)) continue;

            long timeBetween = Duration.between(startTime, departureTime).toMinutes();

            TrainStop trainStop = new TrainStop(train, s, departureTime);
            queue.add(new Pair<>(trainStop, timeBetween));
            prevTrainStopByTrainStop.put(trainStop, null);
            bestTimeByStationIdAndTrainId.put(new Pair<>(s.getStation().getId(), train.getId()), timeBetween);
        }

        if (queue.isEmpty()) return List.of();

        TrainStop foundTrainStop = null;

        while (!queue.isEmpty()) {
            var el = queue.poll();
            TrainStop trainStop = el.getValue0();
            Stop stop = trainStop.stop();
            Train train = trainStop.train();
            long stopTime = el.getValue1();

            if (stop.getStation().getId().equals(stationRepository.findStationByName(stationTo).getId())) {
                foundTrainStop = trainStop;
                break;
            }

            Long known = bestTimeByStationIdAndTrainId.get(new Pair<>(stop.getStation().getId(), train.getId()));
            if (known != null && stopTime > known) continue;

            List<Stop> stopsOfTrain = stopRepository.findStopsByTrain(train.getId());
            reqs++;
            int stopIndex = -1;
            for (int i = 0; i < stopsOfTrain.size(); i++) {
                if (stopsOfTrain.get(i).getId().equals(stop.getId())) {
                    stopIndex = i;
                    break;
                }
            }
            if (stopIndex == -1) continue;
            if (stopIndex + 1 < stopsOfTrain.size()) {
                Stop nextStop = stopsOfTrain.get(stopIndex + 1);
                LocalTime arrival = getStopTime(nextStop, true);
                if (arrival == null) continue;
                long timeBetween = Duration.between(startTime, arrival).toMinutes();
                Long bestTime=bestTimeByStationIdAndTrainId.get(new Pair<>(nextStop.getStation().getId(), train.getId()));
                if (bestTime==null || timeBetween<bestTime) {
                    TrainStop next = new TrainStop(train, nextStop, arrival);
                    bestTimeByStationIdAndTrainId.put(new Pair<>(nextStop.getStation().getId(), train.getId()), timeBetween);
                    prevTrainStopByTrainStop.put(next, trainStop);
                    queue.add(new Pair<>(next, timeBetween));
                }
            }

            LocalTime arrival2 = trainStop.time();
            List<Stop> consideredDepartures = stopRepository.findDeparturesFromStationAfter(stop.getStation().getId(),
                    arrival2.plus(Duration.ofMinutes(5)));
            reqs++;
            for (Stop departureStop : consideredDepartures) {
                Train nextTrain = departureStop.getTrain();
                if (nextTrain.getId().equals(train.getId())) continue;
                if (!isTrainOfOneOfTransporters(nextTrain, transporters)) continue;

                LocalTime departureTime = getStopTime(departureStop, false);
                if (departureTime == null) continue;
                LocalTime arrivalTime=getStopTime(departureStop, true);
                long timeBetween = Duration.between(startTime, arrivalTime).toMinutes();
                Long bestTime = bestTimeByStationIdAndTrainId.get(new Pair<>(departureStop.getStation().getId(),
                        nextTrain.getId()));
                if (bestTime==null || timeBetween<bestTime) {
                    TrainStop next = new TrainStop(nextTrain, departureStop, arrivalTime);
                    bestTimeByStationIdAndTrainId.put(new Pair<>(departureStop.getStation().getId(), nextTrain.getId()),
                            timeBetween);
                    prevTrainStopByTrainStop.put(next, trainStop);
                    queue.add(new Pair<>(next, timeBetween));
                }
            }
        }

        if (foundTrainStop == null) return List.of();

        List<ConnectionSegment> path = new ArrayList<>();
        TrainStop currentTrainStop = foundTrainStop;

        while (true) {
            TrainStop p = prevTrainStopByTrainStop.get(currentTrainStop);
            if (p == null) break;

            Stop from = p.stop();
            Stop to   = currentTrainStop.stop();

            if (!from.getStation().getId().equals(to.getStation().getId())) {
                path.add(new ConnectionSegment(
                        from,
                        getStopTime(from, false),
                        to,
                        getStopTime(to, true),
                        from.getTrain()
                ));
            }
            currentTrainStop = p;
        }

        Collections.reverse(path);
        ConnectionSegment current = null;
        List<ConnectionSegment> path2 = new ArrayList<>();

        for (ConnectionSegment seg : path) {
            if (current == null) {
                current = seg;
                continue;
            }

            if (current.train().getId().equals(seg.train().getId())) {
                current = new ConnectionSegment(
                        current.stopFrom(),
                        current.departure(),
                        seg.stopTo(),
                        seg.arrival(),
                        current.train()
                );
            } else {
                path2.add(current);
                current = seg;
            }
        }

        if (current != null) {
            path2.add(current);
        }
        System.out.println("requests: "+reqs);
        return path2;
    }
    //    @Transactional(timeout = 15)
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
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("H:m");
    @Transactional
    public void importTrain(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new IllegalArgumentException("Brak nazwy pliku");
        filename = filename.replaceFirst("\\.[^.]+$", "");
        String type = filename.replaceAll("\\d", "").toUpperCase();
        String number = filename.replaceAll("\\D", "");
        Train train=new Train();
        Train existing =trainRepository.getTrainByName(type+" "+number);
        if(existing==null)train.setName(type+" "+number);
        else throw new RuntimeException("Pociąg o takiej nazwie już istnieje.");
        train.setName(type+" "+number);
        Transporter transporter = transporterRepository.findByName(type);
        train.setTransporter(transporter);
        Train t=trainRepository.save(train);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(";", -1);
                if (fields.length < 5) {
                    continue;
                }
                String stationName = fields[0].trim();
                String arrival = fields[1].trim();
                String departure = fields[2].trim();
                String platform = fields[3].trim();
                String track = fields[4].trim();
                Station station = stationRepository
                        .findStationByName(stationName);
                if(station== null)
                    throw new RuntimeException("Nie znaleziono stacji: " + stationName);
                System.out.println(t.getName());
                Stop stop = new Stop();
                stop.setStation(station);
                stop.setPlatform(platform);
                stop.setTrack(track);
                stop.setTrain(t);
                if (!arrival.isEmpty()) {
                    stop.setArrivalTime(LocalTime.parse(arrival, TIME_FORMAT));
                }
                if (!departure.isEmpty()) {
                    stop.setDepartureTime(LocalTime.parse(departure, TIME_FORMAT));
                }
                stopRepository.save(stop);
            }
        }
    }
}




