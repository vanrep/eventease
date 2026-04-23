package eventease.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import eventease.Dto.EventoDto;
import eventease.Exception.RecursoNoEncontradoException;
import eventease.Model.Evento;
import eventease.Model.Usuario;
import eventease.Repository.EventoRepository;
import eventease.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

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

        List<Evento> eventos = eventoRepository.findByClienteEmail(email);
        List<EventoDto> eventosDto = new ArrayList<>();

        for (Evento e : eventos) {
            eventosDto.add(entityToDto(e));
        }
        return eventosDto;
    }

    // obtener evento por id
    public EventoDto obtenerPorId(Long id) {
        Optional<Evento> opt = eventoRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Evento no encontrado");
        }
        return entityToDto(opt.get());
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
        return dto;
    }

}
