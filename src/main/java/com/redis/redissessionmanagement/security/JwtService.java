package com.redis.redissessionmanagement.security;

import com.redis.redissessionmanagement.enumarate.Role;
import com.redis.redissessionmanagement.exception.TokenExpiredException;
import com.redis.redissessionmanagement.exception.UnauthorizedException;
import com.redis.redissessionmanagement.service.TokenCacheService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private final TokenCacheService tokenCacheService;

    public JwtService(TokenCacheService tokenCacheService) {
        this.tokenCacheService = tokenCacheService;
    }

    public String generateToken(String email, Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role null olamaz");
        }
        SecretKey key = getSignKey();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", "ROLE_" + role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("Token süresi dolmuş", e.getMessage());
        } catch (Exception e) {
            throw new UnauthorizedException("Geçersiz token", e.getMessage());
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = getClaims(token);
        return resolver.apply(claims);
    }

    public void validateToken(String token) {
        if (tokenCacheService.isTokenBlacklisted(token)) {
            throw new UnauthorizedException("Bu token kara listeye alınmış.");
        }
        if (isTokenExpired(token)) {
            throw new TokenExpiredException("Token süresi dolmuş.");
        }
    }

    public void invalidateToken(String token) {
        long expirationMillis = extractExpiration(token).getTime() - System.currentTimeMillis();
        if (expirationMillis > 0) {
            tokenCacheService.blacklistToken(token, extractExpiration(token));
        }
    }

    private SecretKey getSignKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}
