package eventease.Service;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JSONWebTokenService {

    // clave secreta
    @Value("${jwt.secret}")
    private String secret;

    // 1 hora, expiración del token
    @Value("${jwt.expiration}")
    private long expiration;

    // generar token
    public String generateToken(String email, Long userId, String rol) {
        return Jwts.builder()
                .setSubject(email) // guarda el email dentro del token
                .claim("userId", userId) // guarda el ID numérico
                .claim("rol", rol) // guarda el rol del usuario
                .setIssuedAt(new Date()) // fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 1 hora
                .signWith(getKey()) // firma el token
                .compact();
    }

    // extraer email del token
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // validar token
    public boolean isTokenValid(String token, String email) {
        final String emailToken = extractEmail(token);
        return (emailToken.equals(email) && !isTokenExpired(token));
    }

    // comprobar si expiró
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // leer contenido del token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // generar clave
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
