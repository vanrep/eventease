package eventease.RestController;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eventease.Dto.EventoDto;
import eventease.Dto.UsuarioDto;
import eventease.Service.AdminService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    // Obtiene la lista de usuarios para el panel de administración
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDto>> obtenerUsuarios() {
        return ResponseEntity.ok(adminService.obtenerUsuarios());
    }

    // Obtiene la lista de eventos para el panel de administración
    @GetMapping("/eventos")
    public ResponseEntity<List<EventoDto>> obtenerEventos() {
        return ResponseEntity.ok(adminService.listarEventos());
    }

    // Elimina un usuario por id desde el panel de administración
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        adminService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Elimina un evento por id desde el panel de administración
    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        adminService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }
}