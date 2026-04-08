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

    // crear evento
    public EventoDto crearEvento(EventoDto dto) {

        // buscar cliente por id
        Optional<Usuario> opt = usuarioRepository.findById(dto.getClienteId());

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
