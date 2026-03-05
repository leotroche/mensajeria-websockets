package com.mensajeria.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public Authentication getAuthentication(String token) {

        String username = this.verifyThenGetUsernameFromJwtToken(token);

        // Authenticate to Spring Security
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return validateAndCutToken(bearerToken);
    }

    public String getJwtFromHeader(SimpMessageHeaderAccessor request) {
        String bearerToken = request.getFirstNativeHeader("Authorization");
        return validateAndCutToken(bearerToken);
    }

    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String verifyThenGetUsernameFromJwtToken(String token) {
        // verifies the auth is valid, then returns the username
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            System.out.printf("Validating token %s%n", authToken);
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String validateAndCutToken(String bearerToken) {
        logger.debug("Authorization Header: {}", bearerToken);
        if (isFormattedBearerToken(bearerToken)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthFromHeader(ServerHttpRequest request) {

        String token = request.getHeaders().getFirst("Authorization");
        return validateAndGetAuth(token);
    }

    public Authentication getAuthFromHeader(StompHeaderAccessor accessor) {

        String token = accessor.getFirstNativeHeader("Authorization");
        return validateAndGetAuth(token);
    }

    private Authentication validateAndGetAuth(String token) {
//        if (!isFormattedBearerToken(token)) throw new InvalidTokenException("Invalid token.");
        if (!isFormattedBearerToken(token)) return null;

        token = token.substring(7);

//        if (!validateJwtToken(token)) throw new InvalidTokenException("Invalid token.");
        if (!validateJwtToken(token)) return null;

        return getAuthentication(token);
    }

    private static boolean isFormattedBearerToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }

}