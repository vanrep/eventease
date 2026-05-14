package eventease.Dto;

import eventease.Model.EstadoInvitacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvitacionDto {

    private Long id;

    private EstadoInvitacion estado;

    @NotNull(message = "El id del evento es obligatorio")
    private Long eventoId;

    @NotBlank(message = "El email del asistente es obligatorio")
    @Email
    private String emailAsistente;

    // Se obtienen para el frontend a través del evento en Invitacion.java
    private String eventoTitulo;
    private String eventoFecha;
    private String eventoUbicacion;
    private String clienteEmail;
}