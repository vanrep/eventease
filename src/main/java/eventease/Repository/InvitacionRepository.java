package eventease.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import eventease.Model.Invitacion;

public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {
    
    // Obtener todas las invitaciones de un asistente/usuario
    List<Invitacion> findByAsistenteId(Long asistenteId);

    // Obtener todas las invitaciones de un evento
    List<Invitacion> findByEventoId(Long eventoId);

    // Comprobar si un asistente ya está invitado al mismo evento
    boolean existsByEventoIdAndAsistenteId(Long eventoId, Long asistenteId);
    
}
