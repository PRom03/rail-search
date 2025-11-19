package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Seat;
import org.example.railsearch.Services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatController {
    @Autowired
    private SeatService seatService;
    @GetMapping(value = "/{carNumber}/{trainId}",produces = "application/json")
    public List<Seat> findByTrainAndTrainCar(@PathVariable Integer carNumber, @PathVariable Long trainId) {
        return seatService.findByTrainAndTrainCar(carNumber,trainId);
    }

}
