package org.example.railsearch.Controllers;

import org.example.railsearch.Services.TravelStageService;
import org.example.railsearch.Entities.TravelStage;
import org.example.railsearch.TravelStageCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-stages")
public class TravelStageController {
    @Autowired
    private TravelStageService travelStageService;
    @PostMapping("/")
    public List<TravelStage> create(@RequestBody List<TravelStageCreateDto> dtos) {
        return travelStageService.saveAll(dtos);
  }
  @GetMapping("/ticket/{id}")
    public List<TravelStage> findTravelStagesByTicketId(@PathVariable Integer id) {
        return travelStageService.findTravelStagesByTicketId(id);
  }
    @PostMapping("/reserve")
    public TravelStage reserve(@RequestHeader("Authorization") String token, @RequestBody SeatReservationRequest req) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return travelStageService.reserveSeat(req.trainDateId(), req.carNumber(), req.seatNumber(), req.travelStageId());
    }

    public record SeatReservationRequest(long trainDateId, int carNumber, int seatNumber, Integer travelStageId) {}
}
