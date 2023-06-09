package com.danieldigiovanni.email.utils;

import com.danieldigiovanni.email.customer.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {

    public static String generateToken(Customer customer, Long tokenDurationMillis, String tokenSecretKey) {
        long issuedAtMillis = System.currentTimeMillis();
        long expiredAtMillis = issuedAtMillis + tokenDurationMillis;

        return Jwts.builder()
            .setSubject(Long.toString(customer.getId()))
            .setIssuedAt(new Date(issuedAtMillis))
            .setExpiration(new Date(expiredAtMillis))
            .signWith(
                JwtUtils.getSigningKey(tokenSecretKey),
                SignatureAlgorithm.HS256
            )
            .compact();
    }

    public static Claims extractClaimsFromToken(String jwt, String tokenSecretKey) {
        JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(JwtUtils.getSigningKey(tokenSecretKey))
            .build();

        return jwtParser.parseClaimsJws(jwt).getBody();
    }

    private static Key getSigningKey(String tokenSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
