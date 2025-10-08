package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.Train;
import org.example.railsearch.Services.TrainService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainController {
    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }


    ;
    @RequestMapping(value = { "/search/{stationFrom}/{stationTo}","/search/{stationFrom}/{stationTo}/{startTime}" }, method = RequestMethod.POST, produces = "application/json")
    public List<List<TrainService.ConnectionSegment>> getDistance(@PathVariable String stationFrom, @PathVariable String stationTo,@PathVariable LocalTime startTime, @RequestBody  List<String> transporters) {
        if(startTime==null) startTime = LocalTime.now();
        
        return trainService.findAllConnectionsForDay(stationFrom, stationTo, startTime,transporters);

    }
}
