package eventease.RestController;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eventease.Dto.InvitacionDto;
import eventease.Model.EstadoInvitacion;
import eventease.Service.InvitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invitaciones")
public class InvitacionController {

    private final InvitacionService invitacionService;

    // crear invitación (el cliente invita a un asistente)
    @PostMapping
    public ResponseEntity<InvitacionDto> crearInvitacion(@Valid @RequestBody InvitacionDto dto, Principal principal) {
        String emailCreador = principal.getName();
        InvitacionDto creada = invitacionService.crearInvitacion(dto, emailCreador);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // listar las invitaciones del asistente logueado
    @GetMapping
    public ResponseEntity<List<InvitacionDto>> listarMisInvitaciones(Principal principal) {
        String emailAsistente = principal.getName();
        List<InvitacionDto> misInvitaciones = invitacionService.listarMisInvitaciones(emailAsistente);
        return ResponseEntity.ok(misInvitaciones);
    }

    // aceptar invitación
    @PutMapping("/evento/{eventoId}/aceptar")
    public ResponseEntity<InvitacionDto> aceptarInvitacion(@PathVariable Long eventoId, Principal principal) {
        String emailAsistente = principal.getName();
        // se envia el eventoId (del url), email del token, y el estado del endpoint
        InvitacionDto actualizada = invitacionService.responderInvitacion(eventoId, emailAsistente,
                EstadoInvitacion.ACEPTADA);
        return ResponseEntity.ok(actualizada);
    }

    // rechazar invitación
    @PutMapping("/evento/{eventoId}/rechazar")
    public ResponseEntity<InvitacionDto> rechazarInvitacion(@PathVariable Long eventoId, Principal principal) {
        String emailAsistente = principal.getName();
        // se envia el eventoId (del url), email del token, y el estado del endpoint
        InvitacionDto actualizada = invitacionService.responderInvitacion(eventoId, emailAsistente,
                EstadoInvitacion.RECHAZADA);
        return ResponseEntity.ok(actualizada);
    }


    // ----------- SIMULACIÓN DE LOS ENDPOINTS PARA INVITACIONES DESDE ENLACES PÚBLICOS ---------------
    @PutMapping("/public/aceptar")
    public ResponseEntity<InvitacionDto> aceptarInvitacionPublica(@RequestBody String datos) {
        // "datos" simula los datos cifrados que vienen en el enlace del email
        InvitacionDto actualizada = invitacionService.responderInvitacionPublica(datos,EstadoInvitacion.ACEPTADA);
        return ResponseEntity.ok(actualizada);
    }

    @PutMapping("/public/rechazar")
    public ResponseEntity<InvitacionDto> rechazarInvitacionPublica(@RequestBody String datos) {
        // "datos"" simula los datos cifrados que vienen en el enlace del email
        InvitacionDto actualizada = invitacionService.responderInvitacionPublica(datos,EstadoInvitacion.RECHAZADA);
        return ResponseEntity.ok(actualizada);
    }
}
