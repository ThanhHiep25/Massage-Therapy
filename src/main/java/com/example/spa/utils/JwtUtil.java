
package com.example.spa.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
     // Secret-key configuration
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Token expiration configuration 15 phut
    @Value("${jwt.access-token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;

    // Refresh token expiration configuration 7 ngày
    @Value("${jwt.refresh-token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    // Generate access token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 phút
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Generate access refresh token
    public  String generateRefreshToken(String email) {
        return  Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 ngày
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extract username from JWT token
    public String extractUserMail(String token) {
        return extractClaim(token, Claims::getSubject);
    }


     // Extract expiration date from JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


     // Check if JWT token is valid
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


     // Check if JWT token is expired
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e) {
            System.err.println("Error parsing claims JWT token: " + e.getMessage());
            throw e;
        }
    }


     // Check if JWT token is valid
    public boolean isTokenValid(String token, String email) {
        final String extractedUserMail = extractUserMail(token);
        return (extractedUserMail.equals(email) && !isTokenExpired(token));
    }


     // Check if JWT token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
