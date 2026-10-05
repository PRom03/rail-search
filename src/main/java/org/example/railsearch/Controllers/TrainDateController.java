package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.User;
import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.TrainDateService;
import org.example.railsearch.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/train-date")
public class TrainDateController {
    @Autowired
    private TrainDateService trainDateService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @GetMapping("/{trainId}/{date}")
    public ResponseEntity<?> getIdByTrainIdAndDate(@PathVariable int trainId, @PathVariable LocalDate date) {
        return new ResponseEntity<>(Map.of("trainDateId",trainDateService.getIdByTrainIdAndDate(trainId, date)), HttpStatus.OK);
    }
    @PostMapping("/add-many")
    public ResponseEntity<?> addTrainDateMany( @RequestBody List<TrainDateService.TrainDateRequest> requests,
                                               @RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        trainDateService.addMany(requests);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
