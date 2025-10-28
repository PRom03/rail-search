package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Services.StationService;
import org.example.railsearch.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/stations")
public class StationController {
    @Autowired
    private StationService stationService;

    @GetMapping("/{query}")
    public List<Station> getSuggestedStations(@PathVariable String query){
        return stationService.getSuggestedStations(query);
    }
}
