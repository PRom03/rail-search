package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.User;
import org.example.railsearch.Services.JwtService;

import org.example.railsearch.Services.TrainService;
import org.example.railsearch.Services.UserService;
import org.example.railsearch.SimpleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.util.List;

@RestController
public class TrainController {
    @Autowired
    private TrainService trainService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;



    ;
    @RequestMapping(value = { "/search/{stationFrom}/{stationTo}","/search/{stationFrom}/{stationTo}/{startTime}" }, method = RequestMethod.POST, produces = "application/json")
    public List<List<TrainService.ConnectionSegment>> getDistance(@PathVariable String stationFrom, @PathVariable String stationTo, @PathVariable LocalTime startTime, @RequestBody  List<String> transporters) {
        if(startTime==null) startTime = LocalTime.now();
        return trainService.findAllConnectionsForDay(stationFrom, stationTo, startTime,transporters);

    }
    @GetMapping(value = "/",produces = "application/json")
    public ResponseEntity<?> getTrains(@RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(trainService.findAll(), HttpStatus.OK);
    }
    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> importTrain(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin"))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Plik jest pusty");
        }
        try {
            trainService.importTrain(file);
            return ResponseEntity.ok("Import zakończony sukcesem");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Błąd importu: " + e.getMessage());
        }
    }
    @PatchMapping(value = "/{id}/update",produces = "application/json")
    public ResponseEntity<?> updateTrain(@PathVariable long id,@RequestBody SimpleDto dto) {
        return new ResponseEntity<>(
                trainService.updateTrain(id,
                        dto.name()
                ), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        return new ResponseEntity<>(trainService.findById(id), HttpStatus.OK);
    }
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteTrain(@PathVariable long id,@RequestHeader("Authorization") String token){
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        trainService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @ExceptionHandler(TransactionTimedOutException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSearchTimeout() {
        return "Search timeout.";
    }
}
