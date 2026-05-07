package eventease.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
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

import eventease.Dto.EventoDetallesDto;
import eventease.Exception.NoAutorizadoException;
import eventease.Model.EstadoInvitacion;
import eventease.Model.Evento;
import eventease.Model.Invitacion;
import eventease.Model.Role;
import eventease.Model.Usuario;
import eventease.Repository.EventoRepository;
import eventease.Repository.InvitacionRepository;
import eventease.Repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private InvitacionRepository invitacionRepository;

    @InjectMocks
    private EventoService eventoService;

    @Test
    void obtenerPorId_permitaAlAdminVerDetallesDeCualquierEvento() {
        Long eventoId = 10L;
        String adminEmail = "admin@eventease.com";
        Usuario admin = crearUsuario(1L, adminEmail, Role.ADMIN);
        Usuario creador = crearUsuario(2L, "cliente@eventease.com", Role.CLIENTE);
        Evento evento = crearEvento(eventoId, "Evento admin", creador);
        Invitacion invitacion = crearInvitacion(100L, evento, "guest@eventease.com", EstadoInvitacion.PENDIENTE);

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(usuarioRepository.findByEmail(adminEmail)).thenReturn(Optional.of(admin));
        when(invitacionRepository.findByEventoId(eventoId)).thenReturn(List.of(invitacion));

        EventoDetallesDto resultado = eventoService.obtenerPorId(eventoId, adminEmail);

        assertThat(resultado.getId()).isEqualTo(eventoId);
        assertThat(resultado.getInvitaciones()).hasSize(1);
        assertThat(resultado.getInvitaciones().get(0).getEmailAsistente()).isEqualTo("guest@eventease.com");
        verify(invitacionRepository).findByEventoId(eventoId);
        verify(invitacionRepository, never()).existsByEventoIdAndEmailAsistente(eventoId, adminEmail);
    }

    @Test
    void obtenerPorId_rechazaAUnUsuarioSinRelacionConElEventoSiNoEsAdmin() {
        Long eventoId = 11L;
        String email = "ajeno@eventease.com";
        Usuario usuario = crearUsuario(3L, email, Role.CLIENTE);
        Evento evento = crearEvento(eventoId, "Evento privado", crearUsuario(4L, "owner@eventease.com", Role.CLIENTE));

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(evento));
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(invitacionRepository.existsByEventoIdAndEmailAsistente(eventoId, email)).thenReturn(false);

        assertThrows(NoAutorizadoException.class, () -> eventoService.obtenerPorId(eventoId, email));
        verify(invitacionRepository, never()).findByEventoId(eventoId);
    }

    private Usuario crearUsuario(Long id, String email, Role role) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Usuario " + id);
        usuario.setEmail(email);
        usuario.setPassword("secreto");
        usuario.setRol(role);
        return usuario;
    }

    private Evento crearEvento(Long id, String titulo, Usuario cliente) {
        Evento evento = new Evento();
        evento.setId(id);
        evento.setTitulo(titulo);
        evento.setDescripcion("Descripcion " + id);
        evento.setFecha(LocalDateTime.now().plusDays(10));
        evento.setUbicacion("Ubicacion " + id);
        evento.setCapacidad(100);
        evento.setCliente(cliente);
        evento.setInvitaciones(List.of());
        return evento;
    }

    private Invitacion crearInvitacion(Long id, Evento evento, String emailAsistente, EstadoInvitacion estado) {
        Invitacion invitacion = new Invitacion();
        invitacion.setId(id);
        invitacion.setEvento(evento);
        invitacion.setEmailAsistente(emailAsistente);
        invitacion.setEstado(estado);
        return invitacion;
    }
}