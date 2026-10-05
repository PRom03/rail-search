package org.example.railsearch.Controllers;

import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.TravelStageService;
import org.example.railsearch.Entities.TravelStage;
import org.example.railsearch.Services.UserService;
import org.example.railsearch.TravelStageCreateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-stages")
public class TravelStageController {
    @Autowired
    private TravelStageService travelStageService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody List<TravelStageCreateDto> dtos,@RequestHeader("Authorization") String token) {
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ","");
        if (jwtService.isExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(travelStageService.saveAll(dtos,userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null).getId()), HttpStatus.OK);
  }
  @GetMapping(value = "/ticket/{id}",produces = "application/json")
    public List<TravelStage> findTravelStagesByTicketId(@PathVariable Integer id) {
        return travelStageService.findTravelStagesByTicketId(id);
  }
    @PostMapping(value = "/reserve",produces = "application/json")
    public ResponseEntity<?> reserve(
            @RequestHeader("Authorization") String token,
            @RequestBody SeatReservationRequest req) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }

        token=token.replace("Bearer ","");
        if (jwtService.isExpired(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(
                travelStageService.reserveSeat(
                        req.trainDateId(), req.carNumber(), req.seatNumber(),
                        req.travelStageId()
                ), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalState(IllegalStateException ex) {
        return new ErrorResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return HttpStatus.BAD_REQUEST;
            }

            @Override
            public ProblemDetail getBody() {
                return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,ex.getMessage());
            }
        };
    }
    public record SeatReservationRequest(long trainDateId, int carNumber, int seatNumber, Integer travelStageId) {}
}
