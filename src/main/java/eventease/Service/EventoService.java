package eventease.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // actualizar evento
    public EventoDto actualizarEvento(Long id, EventoDto dto, String email) {
        // buscamos el evento
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        Evento e = opt.get();
        // Verificar que el usuario autenticado sea el dueño
        if (!e.getCliente().getEmail().equals(email)) {
            throw new eventease.Exception.NoAutorizadoException("No tienes permiso para editar este evento");
        }
        // actualizamos el evento
        e.setTitulo(dto.getTitulo());
        e.setDescripcion(dto.getDescripcion());
        e.setFecha(dto.getFecha());
        e.setUbicacion(dto.getUbicacion());
        e.setCapacidad(dto.getCapacidad());

        Evento guardado = eventoRepository.save(e);
        return entityToDto(guardado);
    }

    // eliminar evento
    @Transactional
    public void eliminarEvento(Long id, String email) {
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        Evento e = opt.get();
        // Verificar que el usuario autenticado sea el creador del evento
        if (!e.getCliente().getEmail().equals(email)) {
            throw new eventease.Exception.NoAutorizadoException("No tienes permiso para eliminar este evento");
        }
        // borrar las invitaciones también
        invitacionRepository.deleteByEventoId(id);
        eventoRepository.delete(e);
    }

    // listar eventos por email (usuario logeado)
    public List<EventoDto> listarEventosPorEmail(String email) {
        // eventos creados por el usuario
        List<Evento> eventosPropios = eventoRepository.findByClienteEmail(email);

        // eventos donde aparece el email del usuario (invitaciones)
        List<Invitacion> invitaciones = invitacionRepository.findByEmailAsistente(email);

        List<EventoDto> eventosDto = new ArrayList<>();

        // estado invitación será NULL
        for (Evento e : eventosPropios) {
            eventosDto.add(entityToDto(e));
        }
        for (Invitacion inv : invitaciones) {
            Evento eventoInvitado = inv.getEvento();
            boolean yaExiste = false;
            // evitar duplicados
            for (EventoDto dto : eventosDto) {
                if (dto.getId().equals(eventoInvitado.getId())) {
                    yaExiste = true;
                }
            }
            // si aún no está en la lista, lo añade con el estado
            if (!yaExiste) {
                eventosDto.add(entityToDto(eventoInvitado, inv.getEstado()));
            }
        }
        return eventosDto;
    }

    // obtener evento por id
    public EventoDetallesDto obtenerPorId(Long id, String email) {
        // buscar evento por ID
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        Evento evento = opt.get();

        // comprobamos si el email del cliente (Usuario cliente en entidad Evento) es
        // igual al email en la petición
        boolean esCreador = evento.getCliente().getEmail().equals(email);
        // comprobamos si el email en la petición corresponde a alguna de las
        // invitaciónes del evento con ese id
        boolean esInvitado = invitacionRepository.existsByEventoIdAndEmailAsistente(id, email);
        // si no es creador y si no ha sido invitado, no tiene permisos para ver
        // detalles
        if (!esCreador && !esInvitado) {
            throw new NoAutorizadoException("No tienes permiso para ver este evento");
        }
        // convertimos el evento al EventoDetallesDto que hereda de EventoDto + lista de
        // invitaciones
        EventoDetallesDto dto = entityToDetalleDto(evento);

        // si es creador del evento - añadimos al dto la lista de todas las invitaciones
        // mandadas
        if (esCreador) {
            // buscamos todas las invitaciones del evento
            List<Invitacion> lista = invitacionRepository.findByEventoId(id);

            List<InvitacionDto> invitaciones = new ArrayList<>();
            // las convertimos al dto y añadimos a la lista
            for (Invitacion inv : lista) {
                invitaciones.add(invitacionToDto(inv));
            }
            dto.setInvitaciones(invitaciones);
        }
        return dto;
    }

    // se usa solo si el usuario es creador del evento
    private InvitacionDto invitacionToDto(Invitacion invitacion) {
        InvitacionDto dto = new InvitacionDto();
        dto.setId(invitacion.getId());
        dto.setEstado(invitacion.getEstado());
        dto.setEventoId(invitacion.getEvento().getId());
        dto.setEmailAsistente(invitacion.getEmailAsistente());
        return dto;
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

    // entityToDto pero con el parametro extra de estado
    private EventoDto entityToDto(Evento e, EstadoInvitacion estadoInvitacion) {
        EventoDto dto = entityToDto(e);
        dto.setMiEstadoInvitacion(estadoInvitacion);
        return dto;
    }

    // igual que entityToDto, pero devuelve EventoDetalleDto
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

}
