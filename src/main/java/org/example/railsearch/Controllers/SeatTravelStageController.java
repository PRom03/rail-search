package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.SeatTravelStage;
import org.example.railsearch.Entities.TravelStage;
import org.example.railsearch.Services.SeatTravelStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seat-travel-stage")
public class SeatTravelStageController {
    @Autowired
    private SeatTravelStageService seatTravelStageService;

}
