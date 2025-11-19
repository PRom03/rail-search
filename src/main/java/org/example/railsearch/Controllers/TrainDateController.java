package org.example.railsearch.Controllers;

import org.apache.coyote.Response;
import org.example.railsearch.Services.TrainDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;
@RestController
@RequestMapping("/train-date")
public class TrainDateController {
    @Autowired
    private TrainDateService trainDateService;

    @GetMapping("/{trainId}/{date}")
    public ResponseEntity<?> getIdByTrainIdAndDate(@PathVariable int trainId, @PathVariable LocalDate date) {
        return new ResponseEntity<>(Map.of("trainDateId",trainDateService.getIdByTrainIdAndDate(trainId, date)), HttpStatus.OK);
    }
}
