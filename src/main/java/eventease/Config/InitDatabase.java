package eventease.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import eventease.Model.Role;
import eventease.Model.Usuario;
import eventease.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitDatabase implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        // Si no hay usuarios en la BD, crea algunos de prueba
        crearUsuario("Vanja", "vanja@email.com", Role.ADMIN);
        crearUsuario("Mateo", "mateo@email.com", Role.CLIENTE);
        crearUsuario("Elena", "elena@email.com", Role.CLIENTE);
        crearUsuario("Dani", "dani@email.com", Role.CLIENTE);
    }

    private void crearUsuario(String nombre, String email, Role rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode("123456"));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
