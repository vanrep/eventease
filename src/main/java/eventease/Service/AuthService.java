package eventease.Service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import eventease.Dto.AuthResponseDto;
import eventease.Dto.LoginRequestDto;
import eventease.Exception.NoAutorizadoException;
import eventease.Exception.RecursoNoEncontradoException;
import eventease.Model.Usuario;
import eventease.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JSONWebTokenService jwtService;

    // iniciar sesión y devolver token
    public AuthResponseDto login(LoginRequestDto dto) {

        // buscar usuario por  email
        Optional<Usuario> opt = usuarioRepository.findByEmail(dto.getEmail());

        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        Usuario usuario = opt.get();

        // comprobar contraseña
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new NoAutorizadoException("Contraseña incorrecta");
        }

        // generar token con email e ID
        String token = jwtService.generateToken(usuario.getEmail(), usuario.getId(), usuario.getRol().name());

        return new AuthResponseDto(token);
    }

}
