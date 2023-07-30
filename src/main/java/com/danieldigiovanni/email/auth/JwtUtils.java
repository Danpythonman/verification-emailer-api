package com.danieldigiovanni.email.auth;

import com.danieldigiovanni.email.customer.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    /**
     * Generates a JWT for a given customer. The customer's id will be the
     * subject of the JWT.
     *
     * @param customer            The customer being issued the JWT.
     *                            Their id will be the subject of the JWT.
     * @param tokenDurationMillis The duration of the token in milliseconds.
     * @param tokenSecretKey      The secret key to use when signing the token.
     *
     * @return The generated JWT.
     */
    public String generateToken(Customer customer, Long tokenDurationMillis, String tokenSecretKey) {
        long issuedAtMillis = System.currentTimeMillis();
        long expiredAtMillis = issuedAtMillis + tokenDurationMillis;

        return Jwts.builder()
            .setSubject(Long.toString(customer.getId()))
            .setIssuedAt(new Date(issuedAtMillis))
            .setExpiration(new Date(expiredAtMillis))
            .signWith(
                this.getSigningKey(tokenSecretKey),
                SignatureAlgorithm.HS256
            )
            .compact();
    }

    /**
     * Extracts the claims from a JWT.
     *
     * @param jwt            The JWT from which to extract the claims.
     * @param tokenSecretKey The secret key used when the token was signed.
     *
     * @return The claims of the JWT.
     *
     * @throws IllegalArgumentException If the JWT is null, empty, or only
     *                                  whitespace.
     * @throws MalformedJwtException    If the JWT does not have a valid
     *                                  format.
     * @throws UnsupportedJwtException  If the JWT claims do not have a valid
     *                                  format.
     * @throws ExpiredJwtException      If the JWT is expired.
     * @throws SignatureException       If the JWT signature validation fails.
     */
    public Claims extractClaimsFromToken(String jwt, String tokenSecretKey) throws IllegalArgumentException, MalformedJwtException, UnsupportedJwtException, ExpiredJwtException, SignatureException {
        JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(this.getSigningKey(tokenSecretKey))
            .build();

        return jwtParser.parseClaimsJws(jwt).getBody();
    }

    /**
     * Generates a signing key with the HMAC-SHA algorithm based on the given
     * secret key.
     *
     * @param tokenSecretKey The secret key to be used.
     *
     * @return The signing key.
     */
    private Key getSigningKey(String tokenSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
