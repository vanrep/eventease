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
    @PutMapping("/{id}/aceptar")
    public ResponseEntity<InvitacionDto> aceptarInvitacion(@PathVariable Long id, Principal principal) {
        String emailAsistente = principal.getName();
        InvitacionDto actualizada = invitacionService.responderInvitacion(id, emailAsistente, EstadoInvitacion.ACEPTADA);
        return ResponseEntity.ok(actualizada);
    }

    // rechazar invitación
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<InvitacionDto> rechazarInvitacion(@PathVariable Long id, Principal principal) {
        String emailAsistente = principal.getName();
        InvitacionDto actualizada = invitacionService.responderInvitacion(id, emailAsistente, EstadoInvitacion.RECHAZADA);
        return ResponseEntity.ok(actualizada);
    }
}
