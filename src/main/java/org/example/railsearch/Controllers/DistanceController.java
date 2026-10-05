package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Train;
import org.example.railsearch.Entities.User;
import org.example.railsearch.Services.DistanceService;
import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/distances")
public class DistanceController {

    @Autowired
    private DistanceService distanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;


    public record DistanceResponse(Integer distance,String message) {
    }

    public record TrainResponse(String name) {}
    @PostMapping(value = "/add",produces = "application/json")
    public ResponseEntity<?> addDistance(@RequestBody DistanceService.DistanceDto distanceDto, @RequestHeader("Authorization") String token) {
        if(!token.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElseThrow();
        if(!user.getRole().toString().equals("admin")) {
            System.out.println("adminuś");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(
                distanceService.createDistance(
                        distanceDto.stationFromId(),distanceDto.stationToId(),
                        distanceDto.distance()
                ), HttpStatus.CREATED);
    }
    @PatchMapping(value = "/{id}/update",produces = "application/json")
    public ResponseEntity<?> updateDistance(@PathVariable long id,
                                            @RequestBody DistanceService.DistanceDto distanceDto,
                                            @RequestHeader("Authorization") String token) {
        if(!token.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElseThrow();
        if(!user.getRole().toString().equals("admin")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(
                distanceService.updateDistance(id,
                        distanceDto.stationFromId(),distanceDto.stationToId(),
                        distanceDto.distance()
                ), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        return new ResponseEntity<>(distanceService.findById(id), HttpStatus.OK);
    }
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteDistance(@PathVariable long id,
                                            @RequestHeader("Authorization") String token) {
        if(!token.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElseThrow();
        if(!user.getRole().toString().equals("admin")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        distanceService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @RequestMapping(value="/distance-train/{stationFrom}/{stationTo}/{trainName}",method= RequestMethod.GET,  produces="application/json")
    public DistanceResponse getDistanceForTrain(@PathVariable String stationFrom, @PathVariable String stationTo,@PathVariable String trainName) {
        int distance=Math.round(distanceService.getDistanceForTrain(stationFrom, stationTo, trainName));
        return new DistanceResponse(distance,(distance!=-1)?"success":"fail");
    }
    @RequestMapping(value = "/",produces = "application/json")
    public ResponseEntity<?> findAllDistances(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(distanceService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value="/trains/{stationFrom}/{stationTo}",method = RequestMethod.GET,produces = "application/json")
    public List<TrainResponse> getTrains(@PathVariable String stationFrom, @PathVariable String stationTo) {
        List<List<Train>> trains=distanceService.getTrains(stationFrom,stationTo);
        List<TrainResponse> trainResponses = new ArrayList<>();
        for (int i = 0; i < trains.size(); i++) {
            List<Train> trainz = trains.get(i);
            for (int j = 0; j <trainz.size(); j++) {
                trainResponses.add(new TrainResponse(trainz.get(j).getName()));
            }
        }
        return trainResponses;
    }

}
