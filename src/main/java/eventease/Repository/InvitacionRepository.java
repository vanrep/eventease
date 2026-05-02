package eventease.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eventease.Model.Invitacion;

@Repository
public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {
        
    // Obtener todas las invitaciones de un asistente/usuario por email
    List<Invitacion> findByEmailAsistente(String email);

    // Obtener todas las invitaciones de un evento
    List<Invitacion> findByEventoId(Long eventoId);

    // Comprobar si un asistente ya está invitado al mismo evento
    boolean existsByEventoIdAndEmailAsistente(Long eventoId, String emailAsistente);

    Optional<Invitacion> findByEventoIdAndEmailAsistente(Long eventoId, String emailAsistente);

    void deleteByEventoId(Long eventoId);
}
