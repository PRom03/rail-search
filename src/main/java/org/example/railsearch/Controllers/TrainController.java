package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Services.TrainService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainController {
    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    public record ConnectionResponse(
            Station fromStation,
            LocalTime departure,
            Station toStation,
            LocalTime arrival,
            Train train
    ) {
    }

    ;

    @RequestMapping(value = "/search/{stationFrom}/{stationTo}", method = RequestMethod.GET, produces = "application/json")
    public List<List<TrainService.ConnectionSegment>> getDistance(@PathVariable String stationFrom, @PathVariable String stationTo) {
        LocalTime startTime = LocalTime.now();
        return trainService.findAllConnectionsForDay(stationFrom, stationTo, startTime);

    }
}
