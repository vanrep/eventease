package eventease.Dto;

import java.time.LocalDateTime;

import eventease.Model.EstadoInvitacion;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
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
public class EventoDto {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDateTime fecha;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;

    // No se envía, se obtiene del token principal y guarda el ID del usuario
    private Long clienteId;

    // No se guarda en BD, se obtiene a partir del cliente para mandarlo al frontend
    private String clienteEmail;

    // Cuando se crea un evento es null o pendiente, y luego cambia según el estado elegido
    private EstadoInvitacion miEstadoInvitacion;

}