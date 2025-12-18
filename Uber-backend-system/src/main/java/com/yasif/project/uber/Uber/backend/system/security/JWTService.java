package com.yasif.project.uber.Uber.backend.system.security;

import com.yasif.project.uber.Uber.backend.system.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JWTService {

    @Value("${jwt.secretKey}")
    // Injects the secret key from application properties.
    // This key is used to sign and verify JWT tokens.
    private String jwtSecretKey;


    private SecretKey getSecretKey() {

        // Converts the secret key string into a SecretKey object.
        // Uses HMAC SHA-based signing as required by JWT.
        return Keys.hmacShaKeyFor(
                jwtSecretKey.getBytes(StandardCharsets.UTF_8)
        );
    }


    public String generateAccessToken(User user) {

        // Generates a short-lived access token.
        // Used to authenticate API requests.

        return Jwts.builder()

                // Sets userId as the subject of the token.
                .subject(user.getId().toString())

                // Stores user email inside the token payload.
                .claim("email", user.getEmail())

                // Stores user roles for authorization purposes.
                .claim("roles", user.getRoles().toString())

                // Marks the token creation time.
                .issuedAt(new Date())

                // Sets token expiration (10 minutes).
                // Forces frequent renewal for better security.
                .expiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 10)
                )

                // Signs the token using the secret key.
                .signWith(getSecretKey())

                // Builds the final JWT string.
                .compact();
    }


    public String generateRefreshToken(User user) {

        // Generates a long-lived refresh token.
        // Used only to obtain new access tokens.

        return Jwts.builder()

                // Sets userId as the subject.
                .subject(user.getId().toString())

                // Marks the token creation time.
                .issuedAt(new Date())

                // Sets expiration to ~6 months.
                // Reduces the need for frequent re-login.
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000L * 60 * 60 * 24 * 30 * 6
                        )
                )

                // Signs the refresh token.
                .signWith(getSecretKey())

                // Builds the final JWT string.
                .compact();
    }


    public Long getUserIdFromToken(String token) {

        // Parses and validates the JWT token.
        // Verifies signature and checks expiration.

        Claims claims = Jwts.parser()

                // Uses the same secret key for verification.
                .verifyWith(getSecretKey())

                // Builds the JWT parser.
                .build()

                // Parses the signed JWT.
                .parseSignedClaims(token)

                // Extracts token payload (claims).
                .getPayload();

        // Returns the userId stored in the subject.
        return Long.valueOf(claims.getSubject());
    }
}

