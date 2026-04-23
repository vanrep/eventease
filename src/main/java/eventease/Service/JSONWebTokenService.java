package eventease.Service;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JSONWebTokenService {
    
    // clave secreta 
    private final String SECRET = "mi_clave_super_secreta_para_eventease_2026_con_mas_de_32_caracteres";

    // generar token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // guarda el email dentro del token
                .setIssuedAt(new Date()) // fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
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
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }


}
