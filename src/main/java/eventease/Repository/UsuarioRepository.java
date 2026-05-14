package eventease.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eventease.Model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca un usuario por email para login o autenticación
    Optional<Usuario> findByEmail(String email);

    // Comprueba si un email ya existe antes del registro
    boolean existsByEmail(String email);

}