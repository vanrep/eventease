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

    // Obtiene todos los usuarios para el panel de administración
    public List<UsuarioDto> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDto> usuariosDto = new ArrayList<>();

        // Convierte cada usuario al DTO que usa el frontend
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
    // Elimina un usuario y también sus eventos e invitaciones
    public void eliminarUsuario(Long id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        // Busca los eventos creados por ese usuario
        List<Evento> eventos = eventoRepository.findByClienteId(id);

        // Borra las invitaciones de cada evento antes de borrar los eventos
        for (Evento evento : eventos) {
            invitacionRepository.deleteByEventoId(evento.getId());
        }

        // Borra los eventos del usuario y después el propio usuario
        eventoRepository.deleteAll(eventos);
        usuarioRepository.delete(opt.get());
    }

    // Obtiene todos los eventos para el panel de administración
    public List<EventoDto> listarEventos() {
        List<Evento> eventos = eventoRepository.findAll();
        List<EventoDto> eventosDto = new ArrayList<>();

        // Convierte cada evento al DTO que usa el frontend
        for (Evento evento : eventos) {
            eventosDto.add(entityToDto(evento));
        }
        return eventosDto;
    }

    @Transactional
    // Elimina un evento y también sus invitaciones
    public void eliminarEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado"));

        // Borra primero las invitaciones del evento
        invitacionRepository.deleteByEventoId(id);
        eventoRepository.delete(evento);
    }

    // Convierte la entidad de evento a EventoDto
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