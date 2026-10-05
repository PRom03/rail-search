package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.User;
import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static class LoginRequest {
        private String email;
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }




    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String token)
    {
        if(!token.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        token=token.replace("Bearer ", "");

        User user = userService.getUserByEmail(jwtService.extractEmail(token))
                .orElse(null);

        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return new ResponseEntity<>(userService.getUserByEmail(user.getEmail()), HttpStatus.OK);
    }
    @PatchMapping(value = "/me/edit",produces = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody User user,@RequestHeader("Authorization") String token) {
        if(token.startsWith("Bearer ")) {
            token=token.replace("Bearer ", "");
        }
        else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        var id=userService.getUserByEmail(jwtService.extractEmail(token)).orElseThrow(()->new RuntimeException("User Not Found")).getId();
        return new ResponseEntity<>(userService.updateUser(Long.valueOf(id),user), HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.getUserByEmail(loginRequest.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message","Nieprawidłowe dane logowania.")
            );
        }

        String token = jwtService.generateToken(
                user.getId(),user.getFirstname(),user.getLastname(),user.getEmail()
        );
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));
    }


    @PostMapping("/{userId}/card-token")
    public User setCardToken(@RequestHeader("Authorization") String token,@PathVariable Integer userId,@RequestBody UserService.SaveCardTokenRequest body) {
        if(token == null || !token.startsWith("Bearer ")) {
            return  null;
        }
        return userService.setCardToken(userId, body.cardToken());
    }
    @PostMapping("/card-token")
    public ResponseEntity<?> getCardToken(@RequestHeader("Authorization") String token,@RequestBody UserService.GetCardTokenRequest body) {
        if(token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return new ResponseEntity<>(Map.of("cardToken",userService.getCardToken(Long.valueOf(body.userId()))),HttpStatus.OK);
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


}

