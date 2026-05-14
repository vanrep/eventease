package eventease.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eventease.Model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // Obtiene todos los eventos creados por un cliente concreto
    List<Evento> findByClienteId(Long clienteId);

    // Obtiene todos los eventos creados por el email de un usuario
    List<Evento> findByClienteEmail(String email);

}
