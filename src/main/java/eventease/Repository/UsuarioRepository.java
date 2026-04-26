package eventease.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eventease.Model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  
  
    // Buscar un usuario por email para login/autenticación
    Optional<Usuario> findByEmail(String email);

    // Comprobar si un email ya existe antes del registro
    boolean existsByEmail(String email);

}