package org.example.railsearch.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 ;
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(Integer id, String firstName,String lastName,String email) {
        var claims = new HashMap<String, Object>();
        claims.put("id", id);
        claims.put("firstname", firstName);
        claims.put("lastname", lastName);
        claims.put("email", email);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().get("email",String.class);
    }
    private boolean isExpired(String token) {
        Date exp = Jwts.parser().setSigningKey(key)
                .parseClaimsJws(token).getBody().getExpiration();
        return exp.before(new Date());
    }
}
