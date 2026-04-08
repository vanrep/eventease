package eventease.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import eventease.Exception.ConflictException;
import eventease.Exception.RecursoNoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
     // Error 404 para los recursos no encontrados:

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<String> error404(RecursoNoEncontradoException ex ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());

    }

    // Error 409 para los conflictos

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> error409(ConflictException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ex.getMessage());

    }


}
