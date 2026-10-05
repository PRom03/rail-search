package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Station;
import org.example.railsearch.Entities.User;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.StationService;
import org.example.railsearch.Services.UserService;
import org.example.railsearch.SimpleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/stations")
public class StationController {
    @Autowired
    private StationService stationService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @GetMapping("/q/{query}")
    public List<Station> getSuggestedStations(@PathVariable String query){
        return stationService.getSuggestedStations(query);
    }
    @GetMapping(value = "/",produces = "application/json")
    public ResponseEntity<?> getStations(@RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(stationService.findAll(), HttpStatus.OK);
    }
    @PostMapping(value = "/add",produces = "application/json")
    public ResponseEntity<?> addStation(@RequestBody SimpleDto dto,
            @RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(
                stationService.createStation(
                        dto.name()
                ), HttpStatus.CREATED);
    }
    @PatchMapping(value = "/{id}/update",produces = "application/json")
    public ResponseEntity<?> updateSta(@PathVariable long id,@RequestBody SimpleDto dto,

                                       @RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(
                stationService.updateStation(id,
                        dto.name()
                ), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        return new ResponseEntity<>(stationService.findById(id), HttpStatus.OK);
    }
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteStation(@PathVariable long id,

                                           @RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        stationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
