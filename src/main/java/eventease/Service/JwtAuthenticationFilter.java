package eventease.Service;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JSONWebTokenService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Lee la cabecera Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no hay token o no empieza por Bearer, sigue sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Quita "Bearer "
        String token = authHeader.substring(7);

        // Extrae el email del token
        String email = jwtService.extractEmail(token);

        // Si hay email y aún no hay usuario autenticado en el contexto
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = usuarioDetailsService.loadUserByUsername(email);

            // Valida el token
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                // Crea el objeto de un usuario autenticado para la petición
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // El usuario logueado
                        null, // Las credenciales no hacen falta, el JWT ya ha sido validado
                        userDetails.getAuthorities()); // los roles y permisos

                // Añade información extra como la IP o los detalles de sesión
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Guarda el usuario autenticado en el contexto de esta petición
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continúa al siguiente filtro
        filterChain.doFilter(request, response);
    }
}
