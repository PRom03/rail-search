package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.SeatTravelStage;
import org.example.railsearch.Services.SeatTrainDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/train-seats")
public class SeatTrainDateController {
    @Autowired
    private SeatTrainDateService seatTrainDateService;
    @GetMapping("/{trainDateId}")
    public ResponseEntity<?> hasSeats(@PathVariable Integer trainDateId) {
        return new ResponseEntity<>(Map.of("hasSeats",seatTrainDateService.hasSeats(trainDateId)), HttpStatus.OK);
    }
}
