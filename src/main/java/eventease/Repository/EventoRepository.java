package eventease.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eventease.Model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    // Obtener todos los eventos creados por un cliente/usuario concreto
    List<Evento> findByClienteId(Long clienteId);

    // Obtener todos los eventos creados por el email de un usuario
    List<Evento> findByClienteEmail(String email);


}
