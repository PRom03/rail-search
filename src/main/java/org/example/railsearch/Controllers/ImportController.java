package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.User;
import org.example.railsearch.Services.ImportService;
import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retrieve")
public class ImportController {
    @Autowired
    private ImportService importService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @RequestMapping(value = "/stations",method = RequestMethod.GET)
    public ResponseEntity<?> retrieveStations(@RequestHeader("Authorization")
                                                    String token) {
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        User user=userService.getUserByEmail(jwtService.extractEmail(token)).orElse(null);
        if(user==null||!user.getRole().toString().equals("admin")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        importService.retrieveStations();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
