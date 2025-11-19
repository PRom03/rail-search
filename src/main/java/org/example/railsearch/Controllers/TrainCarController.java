package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.TrainCar;
import org.example.railsearch.Services.TrainCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/train-cars")
public class TrainCarController {
    @Autowired
    private TrainCarService trainCarService;
    @GetMapping(value = "/{trainId}",produces = "application/json")
    public List<TrainCar> findByTrainId(@PathVariable Long trainId) {
        return trainCarService.findByTrainId(trainId);
    }
}
