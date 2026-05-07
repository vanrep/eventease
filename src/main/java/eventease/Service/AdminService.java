package eventease.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eventease.Dto.EventoDto;
import eventease.Dto.UsuarioDto;
import eventease.Exception.RecursoNoEncontradoException;
import eventease.Model.Evento;
import eventease.Model.Usuario;
import eventease.Repository.EventoRepository;
import eventease.Repository.InvitacionRepository;
import eventease.Repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final InvitacionRepository invitacionRepository;

    public List<UsuarioDto> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDto> usuariosDto = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            UsuarioDto dto = new UsuarioDto();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setEmail(usuario.getEmail());
            usuariosDto.add(dto);
        }

        return usuariosDto;
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        List<Evento> eventos = eventoRepository.findByClienteId(id);
        for (Evento evento : eventos) {
            invitacionRepository.deleteByEventoId(evento.getId());
        }
        eventoRepository.deleteAll(eventos);
        usuarioRepository.delete(opt.get());
    }

    public List<EventoDto> listarEventos() {
        List<Evento> eventos = eventoRepository.findAll();
        List<EventoDto> eventosDto = new ArrayList<>();

        for (Evento evento : eventos) {
            eventosDto.add(entityToDto(evento));
        }

        return eventosDto;
    }

    @Transactional
    public void eliminarEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado"));

        invitacionRepository.deleteByEventoId(id);
        eventoRepository.delete(evento);
    }

    private EventoDto entityToDto(Evento evento) {
        EventoDto dto = new EventoDto();
        dto.setId(evento.getId());
        dto.setTitulo(evento.getTitulo());
        dto.setDescripcion(evento.getDescripcion());
        dto.setFecha(evento.getFecha());
        dto.setUbicacion(evento.getUbicacion());
        dto.setCapacidad(evento.getCapacidad());
        dto.setClienteId(evento.getCliente().getId());
        dto.setClienteEmail(evento.getCliente().getEmail());
        return dto;
    }
}