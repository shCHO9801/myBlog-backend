package com.shcho.myBlog.common.util;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    @PostConstruct
    public void init() {
        if(secret == null || secret.length() < 32){
            throw new CustomException(ErrorCode.JWT_KEY_ERROR);
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createToken(String username, String role) {
        return Jwts.builder()
                .header().keyId("myKey").and()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
