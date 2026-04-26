package eventease.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import eventease.Dto.EventoDto;
import eventease.Dto.EventoDetallesDto;
import eventease.Dto.InvitacionDto;
import eventease.Exception.NoAutorizadoException;
import eventease.Exception.RecursoNoEncontradoException;
import eventease.Model.EstadoInvitacion;
import eventease.Model.Evento;
import eventease.Model.Invitacion;
import eventease.Model.Usuario;
import eventease.Repository.EventoRepository;
import eventease.Repository.InvitacionRepository;
import eventease.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InvitacionRepository invitacionRepository;

    public EventoDto crearEvento(EventoDto dto, String email) {

        // buscar cliente por email (del token)
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);

        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        // crear entidad
        Evento e = new Evento();
        e.setTitulo(dto.getTitulo());
        e.setDescripcion(dto.getDescripcion());
        e.setFecha(dto.getFecha());
        e.setUbicacion(dto.getUbicacion());
        e.setCapacidad(dto.getCapacidad());
        e.setCliente(opt.get());

        Evento guardado = eventoRepository.save(e);

        return entityToDto(guardado);
    }

    // listar eventos por email (usuario logeado)
    public List<EventoDto> listarEventosPorEmail(String email) {
        List<Evento> eventosPropios = eventoRepository.findByClienteEmail(email);
        List<Invitacion> invitaciones = invitacionRepository.findByEmailAsistente(email);

        List<EventoDto> eventosDto = new ArrayList<>();

        for (Evento e : eventosPropios) {
            eventosDto.add(entityToDto(e));
        }

        for (Invitacion inv : invitaciones) {
            Evento eventoInvitado = inv.getEvento();

            boolean yaExiste = false;

            for (EventoDto dto : eventosDto) {
                if (dto.getId().equals(eventoInvitado.getId())) {
                    yaExiste = true;
                }
            }

            if (!yaExiste) {
                eventosDto.add(entityToDto(eventoInvitado, inv.getEstado()));
            }
        }

        return eventosDto;
    }

    // obtener evento por id
    public EventoDetallesDto obtenerPorId(Long id, String email) {
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }

        Evento evento = opt.get();
        boolean esCreador = evento.getCliente().getEmail().equals(email);
        boolean esInvitado = invitacionRepository.existsByEventoIdAndEmailAsistente(id, email);

        if (!esCreador && !esInvitado) {
            throw new NoAutorizadoException("No tienes permiso para ver este evento");
        }

        EventoDetallesDto dto = entityToDetalleDto(evento);

        if (esCreador) {
            List<InvitacionDto> invitaciones = invitacionRepository.findByEventoId(id)
                .stream()
                .map(this::invitacionToDto)
                .toList();
            dto.setInvitaciones(invitaciones);
        }

        return dto;
    }

    private EventoDetallesDto entityToDetalleDto(Evento e) {
        EventoDetallesDto dto = new EventoDetallesDto();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setFecha(e.getFecha());
        dto.setUbicacion(e.getUbicacion());
        dto.setCapacidad(e.getCapacidad());
        dto.setClienteId(e.getCliente().getId());
        dto.setClienteEmail(e.getCliente().getEmail());
        return dto;
    }

    private InvitacionDto invitacionToDto(Invitacion invitacion) {
        InvitacionDto dto = new InvitacionDto();
        dto.setId(invitacion.getId());
        dto.setEstado(invitacion.getEstado());
        dto.setEventoId(invitacion.getEvento().getId());
        dto.setEmailAsistente(invitacion.getEmailAsistente());
        return dto;
    }

    // actualizar evento
    public EventoDto actualizarEvento(Long id, EventoDto dto, String email) {
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }

        Evento e = opt.get();
        // Verificar que el usuario autenticado sea el dueño
        if (!e.getCliente().getEmail().equals(email)) {
            throw new eventease.Exception.NoAutorizadoException("No tienes permiso para editar este evento");
        }

        e.setTitulo(dto.getTitulo());
        e.setDescripcion(dto.getDescripcion());
        e.setFecha(dto.getFecha());
        e.setUbicacion(dto.getUbicacion());
        e.setCapacidad(dto.getCapacidad());

        Evento guardado = eventoRepository.save(e);
        return entityToDto(guardado);
    }

    // eliminar evento
    public void eliminarEvento(Long id, String email) {
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }

        Evento e = opt.get();
        // Verificar que el usuario autenticado sea el dueño
        if (!e.getCliente().getEmail().equals(email)) {
            throw new eventease.Exception.NoAutorizadoException("No tienes permiso para eliminar este evento");
        }

        eventoRepository.delete(e);
    }

    // convertir entidad a dto
    private EventoDto entityToDto(Evento e) {
        EventoDto dto = new EventoDto();
        dto.setId(e.getId());
        dto.setTitulo(e.getTitulo());
        dto.setDescripcion(e.getDescripcion());
        dto.setFecha(e.getFecha());
        dto.setUbicacion(e.getUbicacion());
        dto.setCapacidad(e.getCapacidad());
        dto.setClienteId(e.getCliente().getId());
        dto.setClienteEmail(e.getCliente().getEmail());
        return dto;
    }

    private EventoDto entityToDto(Evento e, EstadoInvitacion estadoInvitacion) {
        EventoDto dto = entityToDto(e);
        dto.setMiEstadoInvitacion(estadoInvitacion);
        return dto;
    }
}
