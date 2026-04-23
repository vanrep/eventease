package eventease.RestController;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eventease.Dto.EventoDto;
import eventease.Service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    // listar eventos del usuario logeado
    @GetMapping
    public ResponseEntity<List<EventoDto>> listarEventos(Principal principal) {
        String email = principal.getName();
        List<EventoDto> eventos = eventoService.listarEventosPorEmail(email);
        return ResponseEntity.ok(eventos);
    }

    // crear evento
    @PostMapping
    public ResponseEntity<EventoDto> crearEvento(@Valid @RequestBody EventoDto dto, Principal principal) {
        String email = principal.getName();
        EventoDto creado = eventoService.crearEvento(dto, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    // obtener evento por id
    @GetMapping("/{id}")
    public ResponseEntity<EventoDto> obtenerEvento(@PathVariable Long id) {
        EventoDto evento = eventoService.obtenerPorId(id);
        return ResponseEntity.ok(evento);
    }

    // actualizar evento
    @PutMapping("/{id}")
    public ResponseEntity<EventoDto> actualizarEvento(@PathVariable Long id, @Valid @RequestBody EventoDto dto,
            Principal principal) {
        String email = principal.getName();
        EventoDto actualizado = eventoService.actualizarEvento(id, dto, email);
        return ResponseEntity.ok(actualizado);
    }

    // eliminar evento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        eventoService.eliminarEvento(id, email);
        return ResponseEntity.noContent().build();
    }

}
