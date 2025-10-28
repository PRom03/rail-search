package org.example.railsearch.Controllers;

import org.example.railsearch.Services.TicketService;
import org.example.railsearch.Services.TravelStageService;
import org.example.railsearch.Ticket;
import org.example.railsearch.TravelStage;
import org.example.railsearch.TravelStageCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/travel-stages")
public class TravelStageController {
    @Autowired
    private TravelStageService travelStageService;
    @PostMapping("/")
    public List<TravelStage> create(@RequestBody List<TravelStageCreateDto> dtos) {
        return travelStageService.saveAll(dtos);
  }
}
