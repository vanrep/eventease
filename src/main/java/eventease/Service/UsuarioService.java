package eventease.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import eventease.Dto.UsuarioDto;
import eventease.Exception.ConflictException;
import eventease.Model.Role;
import eventease.Model.Usuario;
import eventease.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // registrar un usuario
    public UsuarioDto registrarUsuario(UsuarioDto dto) {

        // comprobar si el email ya está registrado
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }
        // crea usuario nuevo
        Usuario u = new Usuario();
       
        // el nombre es opcional
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            u.setNombre(null);  // guarda null
        } else {
            u.setNombre(dto.getNombre().trim());
        }
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        // El registro público crea siempre usuarios CLIENTE.
        u.setRol(Role.CLIENTE);

        // guarda usuario
        Usuario guardado = usuarioRepository.save(u);

        // devuelve UsuarioDto (con ID, pero SIN contraseña)
        return entityToDto(guardado);
    }

    // entity -> DTO
    private UsuarioDto entityToDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        return dto;
    }

}
