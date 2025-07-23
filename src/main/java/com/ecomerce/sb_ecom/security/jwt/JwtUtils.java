package com.ecomerce.sb_ecom.security.jwt;

import com.ecomerce.sb_ecom.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    // This is the secret key used for signing JWTs. It is fetched from the application.properties file.
    // its is applied during the time of token creation based on the credentials and no one without the secret key
    // can tamper with the token
    // if the token has been tampered with, the signature won't match


    @Value("${spring.app.jwtExpirations}")  // values to be configured in the application.properties resources file
    private int jwtExpirationsMs;
    // this value stored within the application.properties file is used to set the expiration date of the token created with user credentials
    // once the time limit has been crossed the generated token in revoked and set invalid
    // the user has to sign-in again to get the new jwt token

    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails){// UserDetails represents a single user with some property or attributes
        String jwt = generateTokenFromUsername(userDetails);
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api")
                .maxAge(24 * 60 * 60)
                .httpOnly(false)
                .build();
        return cookie;
    }

    public ResponseCookie getCleanJwtCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
        return cookie;
    }
    
//    public String getJwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization header: {}", bearerToken);
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // Remove bearer prefix
//        }
//        return null;
//    }

    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)  // Sets the subject (username) of the token
                .issuedAt(new Date())  // Sets the issued time of the token
                .expiration(new Date((new Date()).getTime() + jwtExpirationsMs))  // Sets expiration time
                .signWith(key())  // Signs the token with a secret key
                .compact();  // Converts it into a final, usable JWT string
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser() // begins the parsing of the token
                .verifyWith((SecretKey) key()) // tells it to verify the token signature with the secret key
                .build().parseSignedClaims(token) // starts the build for parsed claims
                .getPayload().getSubject(); // gets the subject from the payload
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Jwt token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Jwt token is unsupported: {}", e.getMessage());
        }
        return false;
    }
}
