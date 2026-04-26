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

    // REGISTER
    public UsuarioDto registrarUsuario(UsuarioDto dto) {

        // si el email ya existe
         if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("El email ya está registrado");
        }

        // si el DNI ya existe

        // crea usuario nuevo
        Usuario u = new Usuario();

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            u.setNombre(null);
        } else {
            u.setNombre(dto.getNombre().trim());
        }
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        // El registro público crea siempre usuarios CLIENTE.
        u.setRol(Role.CLIENTE);

        // guarda usuario
        Usuario guardado = usuarioRepository.save(u);
        
        // devuelve UsuarioDto
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
