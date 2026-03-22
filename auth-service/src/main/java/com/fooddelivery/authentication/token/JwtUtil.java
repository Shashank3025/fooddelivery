package com.fooddelivery.authentication.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil 
{
    // ✅ Make sure key is at least 32 characters
    private final String SECRET = "MY_SECRET_KEY_MY_SECRET_KEY_12345";

    // ✅ Generate signing key
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ Generate JWT Token
    public String generateToken(String username)
    {
        return Jwts.builder()
                .setSubject(username) // username stored
                .setIssuedAt(new Date()) // created time
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS))) // expiry
                .signWith(getSigningKey()) // 🔐 sign with key
                .compact(); // convert to string
    }
    
    public String validateToken(String token) {
        // Convert secret to key
        Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        
        // Parse token and get subject (username)
        String username= Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
        return username;
    }
}