
package com.pranav.gympulse.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String secretKey;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate JWT
    public String generateToken(String user) {

        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(
                        System.currentTimeMillis() + 90 * 60 * 1000L
                ))
                .and()
                .signWith(getKey())
                .compact();
    }

    // Extract username
    public String extractUsername(String jwtToken) {
        return extractAllClaims(jwtToken).getSubject();
    }

    // Validate JWT
    public boolean isValidToken(
            String token,
            UserDetails userDetails
    ) {

        try {
            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && extractExpiration(token).after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extract expiration
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
