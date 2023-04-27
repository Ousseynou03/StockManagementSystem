package com.dione.cafe.stockmanagement.JWT;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtUtil {

    private String secretKey = "nedioITConsulting";

    public String extracUsername(String token){
        return extracClaims(token, Claims::getSubject);
    }

    //Date d'expiration
    public Date extractExpiration(String token){
        return extracClaims(token, Claims::getExpiration);
    }

    public <T> T extracClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJwt(token).getBody();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }


    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*10))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public String generateToken(String username, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    //Validation du token
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extracUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}