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
import eventease.Model.Role;
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
        // Busca el cliente por el email del token
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }
        // Crea la entidad
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

    // Actualiza el evento
    public EventoDto actualizarEvento(Long id, EventoDto dto, String email) {
        // Busca el evento
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        Evento e = opt.get();
        // Verifica que el usuario autenticado sea el dueño
        if (!e.getCliente().getEmail().equals(email)) {
            throw new eventease.Exception.NoAutorizadoException("No tienes permiso para editar este evento");
        }
        // Actualiza el evento
        e.setTitulo(dto.getTitulo());
        e.setDescripcion(dto.getDescripcion());
        e.setFecha(dto.getFecha());
        e.setUbicacion(dto.getUbicacion());
        e.setCapacidad(dto.getCapacidad());

        Evento guardado = eventoRepository.save(e);
        return entityToDto(guardado);
    }

    // Elimina el evento
    @Transactional
    public void eliminarEvento(Long id, String email) {
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        Evento e = opt.get();
        // Verifica que el usuario autenticado sea el creador del evento
        if (!e.getCliente().getEmail().equals(email)) {
            throw new eventease.Exception.NoAutorizadoException("No tienes permiso para eliminar este evento");
        }
        // Borra también las invitaciones
        invitacionRepository.deleteByEventoId(id);
        eventoRepository.delete(e);
    }

    // Lista los eventos por email del usuario logueado
    public List<EventoDto> listarEventosPorEmail(String email) {
        List<Evento> eventosPropios;
        eventosPropios = eventoRepository.findByClienteEmail(email);

        // Eventos donde aparece el email del usuario en las invitaciones
        List<Invitacion> invitaciones = invitacionRepository.findByEmailAsistente(email);

        List<EventoDto> eventosDto = new ArrayList<>();

        // El estado de invitación será null
        for (Evento e : eventosPropios) {
            eventosDto.add(entityToDto(e));
        }
        for (Invitacion inv : invitaciones) {
            Evento eventoInvitado = inv.getEvento();
            boolean yaExiste = false;
            // Evita duplicados
            for (EventoDto dto : eventosDto) {
                if (dto.getId().equals(eventoInvitado.getId())) {
                    yaExiste = true;
                }
            }
            // Si aún no está en la lista, lo añade con el estado
            if (!yaExiste) {
                eventosDto.add(entityToDto(eventoInvitado, inv.getEstado()));
            }
        }
        return eventosDto;
    }

    // Obtiene un evento por id
    public EventoDetallesDto obtenerPorId(Long id, String email) {
        // Busca el evento por ID
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        Evento evento = opt.get();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        boolean esAdmin = usuario.getRol() == Role.ADMIN;

        // Comprueba si el email del cliente del evento es igual al email de la petición
        boolean esCreador = evento.getCliente().getEmail().equals(email);
        // Comprueba si el email de la petición corresponde a alguna de las
        // invitaciones del evento con ese id
        boolean esInvitado = !esAdmin && invitacionRepository.existsByEventoIdAndEmailAsistente(id, email);
        // Si no es creador y no ha sido invitado, no tiene permisos para ver los
        // detalles
        if (!esAdmin && !esCreador && !esInvitado) {
            throw new NoAutorizadoException("No tienes permiso para ver este evento");
        }
        // Convierte el evento a EventoDetallesDto con su lista de invitaciones
        EventoDetallesDto dto = entityToDetalleDto(evento);

        // Si es creador o admin, añade al DTO la lista de invitaciones enviadas
        if (esCreador || esAdmin) {
            // Busca todas las invitaciones del evento
            List<Invitacion> lista = invitacionRepository.findByEventoId(id);

            List<InvitacionDto> invitaciones = new ArrayList<>();
            // Las convierte a DTO y las añade a la lista
            for (Invitacion inv : lista) {
                invitaciones.add(invitacionToDto(inv));
            }
            dto.setInvitaciones(invitaciones);
        }
        return dto;
    }

    // Se usa solo si el usuario es creador del evento
    private InvitacionDto invitacionToDto(Invitacion invitacion) {
        InvitacionDto dto = new InvitacionDto();
        dto.setId(invitacion.getId());
        dto.setEstado(invitacion.getEstado());
        dto.setEventoId(invitacion.getEvento().getId());
        dto.setEmailAsistente(invitacion.getEmailAsistente());
        return dto;
    }

    // Convierte la entidad a DTO
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

    // Igual que entityToDto, pero con el parámetro extra de estado
    private EventoDto entityToDto(Evento e, EstadoInvitacion estadoInvitacion) {
        EventoDto dto = entityToDto(e);
        dto.setMiEstadoInvitacion(estadoInvitacion);
        return dto;
    }

    // Igual que entityToDto, pero devuelve EventoDetallesDto
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
