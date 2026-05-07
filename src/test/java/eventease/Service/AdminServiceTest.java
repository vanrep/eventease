package eventease.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import eventease.Exception.RecursoNoEncontradoException;
import eventease.Model.Evento;
import eventease.Model.Usuario;
import eventease.Repository.EventoRepository;
import eventease.Repository.InvitacionRepository;
import eventease.Repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private InvitacionRepository invitacionRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void eliminarUsuario_borraSusEventosEInvitacionesAntesDelUsuario() {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Evento evento = new Evento();
        evento.setId(10L);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findByClienteId(usuarioId)).thenReturn(List.of(evento));

        adminService.eliminarUsuario(usuarioId);

        verify(invitacionRepository).deleteByEventoId(10L);
        verify(eventoRepository).deleteAll(List.of(evento));
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void eliminarUsuario_lanzaErrorSiNoExiste() {
        Long usuarioId = 99L;
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> adminService.eliminarUsuario(usuarioId));
    }

    @Test
    void listarEventos_devuelveTodosLosEventosComoEventoDto() {
        Usuario creador = new Usuario();
        creador.setId(7L);
        creador.setEmail("cliente@eventease.com");

        Evento evento = new Evento();
        evento.setId(10L);
        evento.setTitulo("Evento admin");
        evento.setDescripcion("Descripcion");
        evento.setFecha(LocalDateTime.now().plusDays(5));
        evento.setUbicacion("Madrid");
        evento.setCapacidad(100);
        evento.setCliente(creador);

        when(eventoRepository.findAll()).thenReturn(List.of(evento));

        var eventos = adminService.listarEventos();

        assertThat(eventos).hasSize(1);
        assertThat(eventos.get(0).getId()).isEqualTo(10L);
        assertThat(eventos.get(0).getClienteId()).isEqualTo(7L);
        assertThat(eventos.get(0).getClienteEmail()).isEqualTo("cliente@eventease.com");
    }

    @Test
    void eliminarEvento_borraInvitacionesYLuegoElEvento() {
        Long eventoId = 15L;
        Evento evento = new Evento();
        evento.setId(eventoId);

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));

        adminService.eliminarEvento(eventoId);

        verify(invitacionRepository).deleteByEventoId(eventoId);
        verify(eventoRepository).delete(evento);
    }

    @Test
    void eliminarEvento_lanzaErrorSiNoExiste() {
        Long eventoId = 44L;
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> adminService.eliminarEvento(eventoId));
    }
}