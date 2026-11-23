package com.example.lmchecker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);
    public static final String DEFAULT_DEV_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @Autowired
    private Environment environment;

    @Value("${app.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String jwtSecret;

    @PostConstruct
    public void validateSecurityConfiguration() {
        if (environment != null) {
            boolean isProd = Arrays.stream(environment.getActiveProfiles())
                    .anyMatch(p -> p.equalsIgnoreCase("prod") || p.equalsIgnoreCase("production"));
            if (isProd && DEFAULT_DEV_SECRET.equals(jwtSecret)) {
                throw new IllegalStateException("CRITICAL SECURITY ERROR: Production profile cannot use default development JWT secret. Set JWT_SECRET environment variable.");
            } else if (DEFAULT_DEV_SECRET.equals(jwtSecret)) {
                log.warn("SECURITY NOTICE: Running with default development JWT secret. Override JWT_SECRET in production.");
            }
        }
    }

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (Exception e) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUsernameAndRole(userPrincipal.getUsername(), userPrincipal.getRole().name(), jwtExpirationMs);
    }

    public String generateTokenFromUsernameAndRole(String username, String role, long expirationMs) {
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + expirationMs))
            .signWith(getSigningKey())
            .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
            .subject(username)
            .claim("type", "REFRESH")
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + refreshExpirationMs))
            .signWith(getSigningKey())
            .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token format: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
