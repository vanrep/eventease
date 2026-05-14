package eventease.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import eventease.Dto.InvitacionDto;
import eventease.Exception.ConflictException;
import eventease.Exception.NoAutorizadoException;
import eventease.Exception.RecursoNoEncontradoException;
import eventease.Model.EstadoInvitacion;
import eventease.Model.Evento;
import eventease.Model.Invitacion;
import eventease.Repository.EventoRepository;
import eventease.Repository.InvitacionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitacionService {

    private final InvitacionRepository invitacionRepository;
    private final EventoRepository eventoRepository;

    public InvitacionDto crearInvitacion(InvitacionDto dto, String emailCreador) {
        // Busca el evento por el id del DTO
        Optional<Evento> eventoOpt = eventoRepository.findById(dto.getEventoId());
        if (eventoOpt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        Evento evento = eventoOpt.get();

        // Verifica que el usuario que invita sea el creador del evento
        if (!evento.getCliente().getEmail().equals(emailCreador)) {
            throw new NoAutorizadoException("No tienes permiso para invitar a este evento");
        }

        // Verifica que no se invite dos veces a la misma persona al mismo evento
        if (invitacionRepository.existsByEventoIdAndEmailAsistente(evento.getId(), dto.getEmailAsistente())) {
            throw new ConflictException("Este usuario ya ha sido invitado a este evento");
        }

        // Crea la invitación
        Invitacion invitacion = new Invitacion();
        invitacion.setEvento(evento);
        invitacion.setEmailAsistente(dto.getEmailAsistente());
        invitacion.setEstado(EstadoInvitacion.PENDIENTE); // Estado pendiente por defecto para nuevas invitaciones

        Invitacion guardada = invitacionRepository.save(invitacion);
        return entityToDto(guardada);
    }

    // Lista todas las invitaciones del usuario registrado
    public List<InvitacionDto> listarMisInvitaciones(String emailAsistente) {
        List<Invitacion> invitaciones = invitacionRepository.findByEmailAsistente(emailAsistente);
        List<InvitacionDto> dtos = new ArrayList<>();
        for (Invitacion inv : invitaciones) {
            dtos.add(entityToDto(inv));
        }
        return dtos;
    }

    public InvitacionDto responderInvitacion(Long eventoId, String emailAsistente, EstadoInvitacion nuevoEstado) {
        // Busca la invitación con el id del evento y el correo del invitado
        Optional<Invitacion> opt = invitacionRepository.findByEventoIdAndEmailAsistente(eventoId, emailAsistente);

        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Invitación no encontrada");
        }
        Invitacion invitacion = opt.get();
        // Cambia el estado
        invitacion.setEstado(nuevoEstado);
        Invitacion actualizada = invitacionRepository.save(invitacion);
        return entityToDto(actualizada);
    }

    // Rellena todas las propiedades de InvitacionDto
    private InvitacionDto entityToDto(Invitacion i) {
        InvitacionDto dto = new InvitacionDto();
        dto.setId(i.getId());
        dto.setEstado(i.getEstado());
        dto.setEventoId(i.getEvento().getId());
        dto.setEmailAsistente(i.getEmailAsistente());
        dto.setEventoTitulo(i.getEvento().getTitulo());
        dto.setEventoFecha(i.getEvento().getFecha().toString());
        dto.setEventoUbicacion(i.getEvento().getUbicacion());
        dto.setClienteEmail(i.getEvento().getCliente().getEmail());
        return dto;
    }

    public InvitacionDto responderInvitacionPublica(String datos, EstadoInvitacion nuevoEstado) {
        // "datos" simula los datos cifrados del enlace del email
        // En una versión real, aquí se descifraría y se obtendrían el id del evento y
        // el email del invitado
        Long eventoId = 123L; // valor simulado
        String emailAsistente = "test@email.com"; // valor simulado

        // Busca la invitación con el evento y el email del invitado
        Optional<Invitacion> opt = invitacionRepository.findByEventoIdAndEmailAsistente(eventoId, emailAsistente);

        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Invitación no encontrada");
        }

        Invitacion invitacion = opt.get();

        // Cambia el estado a ACEPTADA o RECHAZADA
        invitacion.setEstado(nuevoEstado);

        Invitacion actualizada = invitacionRepository.save(invitacion);

        return entityToDto(actualizada);
    }
}
