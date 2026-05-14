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

    // Inicia sesión y devuelve el token
    public AuthResponseDto login(LoginRequestDto dto) {

        // Busca el usuario por email
        Optional<Usuario> opt = usuarioRepository.findByEmail(dto.getEmail());

        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        Usuario usuario = opt.get();

        // Comprueba la contraseña
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new NoAutorizadoException("Contraseña incorrecta");
        }

        // Genera el token con el email y el ID
        String token = jwtService.generateToken(usuario.getEmail(), usuario.getId(), usuario.getRol().name());

        return new AuthResponseDto(token);
    }

}
