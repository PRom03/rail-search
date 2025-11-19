package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Train;
import org.example.railsearch.Services.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DistanceController {

    @Autowired
    private DistanceService distanceService;



    public record DistanceResponse(Integer distance,String message) {
    }

    public record TrainResponse(String name) {}

    @RequestMapping(value="/distance-train/{stationFrom}/{stationTo}/{trainName}",method= RequestMethod.GET,  produces="application/json")
    public DistanceResponse getDistanceForTrain(@PathVariable String stationFrom, @PathVariable String stationTo,@PathVariable String trainName) {
        int distance=Math.round(distanceService.getDistanceForTrain(stationFrom, stationTo, trainName));
        return new DistanceResponse(distance,(distance!=-1)?"success":"fail");
    }


    @RequestMapping(value="/trains/{stationFrom}/{stationTo}",method = RequestMethod.GET,produces = "application/json")
    public List<TrainResponse> getTrains(@PathVariable String stationFrom, @PathVariable String stationTo) {
        List<List<Train>> trains=distanceService.getTrains(stationFrom,stationTo);
        List<TrainResponse> trainResponses = new ArrayList<>();
        for (int i = 0; i < trains.size(); i++) {
            List<Train> trainz = trains.get(i);
            for (int j = 0; j <trainz.size(); j++) {
                trainResponses.add(new TrainResponse(trainz.get(j).getName()));
            }
        }
        return trainResponses;
    }

}
