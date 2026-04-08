package eventease.RestController;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<List<EventoDto>> listarEventos (Principal principal){
        String email = principal.getName();
        List<EventoDto> eventos = eventoService.listarEventosPorEmail(email);
        return ResponseEntity.ok(eventos);
    }

    // crear evento
    @PostMapping
    public ResponseEntity<EventoDto> crearEvento (@Valid @RequestBody EventoDto dto){
        EventoDto creado = eventoService.crearEvento(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

}
