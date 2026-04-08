package eventease.Dto;

import eventease.Model.EstadoInvitacion;
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

    @NotNull(message = "El estado es obligatorio")
    private EstadoInvitacion estado;

    @NotNull(message = "El id del evento es obligatorio")
    private Long eventoId;

    @NotNull(message = "El id del asistente es obligatorio")
    private Long asistenteId;
}