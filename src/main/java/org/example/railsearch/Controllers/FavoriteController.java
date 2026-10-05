package org.example.railsearch.Controllers;

import org.example.railsearch.Entities.Favorite;
import org.example.railsearch.Services.FavoriteService;
import org.example.railsearch.Services.JwtService;
import org.example.railsearch.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @GetMapping("/my")
    public List<Favorite> findByUser_Id(@RequestHeader("Authorization") String token) {
        if(!token.startsWith("Bearer "))return List.of();
        token=token.replace("Bearer ", "");
        if(  jwtService.isExpired(token)) return List.of();
        return favoriteService.findByUser_Id(userService.getUserByEmail((jwtService.extractEmail(token))).orElse(null).getId());
    }
    @PostMapping("/add")
    public Favorite addFavorite(@RequestHeader("Authorization") String token, @RequestBody FavoriteService.FavoriteCreateDto favorite) {
        if(!token.startsWith("Bearer ")) return null;
        token=token.replace("Bearer ", "");
        if(  jwtService.isExpired(token)) return null;
        return favoriteService.add(favorite,userService.getUserByEmail((jwtService.extractEmail(token))).orElse(null).getId());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFavorite(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        if(!token.startsWith("Bearer ")) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        token=token.replace("Bearer ", "");
        if(  jwtService.isExpired(token)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        favoriteService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
