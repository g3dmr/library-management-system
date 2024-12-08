package com.librarymanagement.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static final String SECRET = "2D4A614E645267556B58703273357638792F423F4428472B4B6250655368566D";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    /* @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;  */

    public static String generateToken(String username) {
        String token = "";
        try{
            token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(key(),SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception exp) {
            logger.debug("Token Generate Exception :"+exp);
        }
        return token;
    }

    private static Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }

    public static String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static  boolean validateAuthToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(SECRET)
                    .build().parseClaimsJws(token);
            return true;
        } catch(Exception exp) {
            return false;
        }
    }
}
