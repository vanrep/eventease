package eventease.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import eventease.Dto.UsuarioDto;
import eventease.Model.Usuario;
import eventease.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDto registrarUsuario(UsuarioDto dto) {

         if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (usuarioRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        Usuario u = new Usuario();

        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setDni(dto.getDni());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setRol(dto.getRol());

        Usuario guardado = usuarioRepository.save(u);
        
        return entityToDto(guardado);
    }


    private UsuarioDto entityToDto(Usuario u) {
    UsuarioDto dto = new UsuarioDto();
    dto.setId(u.getId());
    dto.setNombre(u.getNombre());
    dto.setEmail(u.getEmail());
    dto.setDni(u.getDni());
    dto.setRol(u.getRol());
    return dto;
}

}
