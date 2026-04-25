package eventease.RestController;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import eventease.Dto.UsuarioDto;
import eventease.Service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UsuarioController {
    

    private final UsuarioService usuarioService;


    @PostMapping("/register")
    public ResponseEntity<UsuarioDto> register(@Valid @RequestBody UsuarioDto dto){
        
        UsuarioDto usuarioGuardado = usuarioService.registrarUsuario(dto);

        return ResponseEntity
            .created(URI.create("/usuarios/" + usuarioGuardado.getDni()))
            .body(usuarioGuardado);
    }
        
    
        
       





}
