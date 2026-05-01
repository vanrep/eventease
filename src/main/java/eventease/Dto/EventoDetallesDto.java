package eventease.Dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoDetallesDto extends EventoDto {
    
    // detalles de un evento + todas las invitaciones relacionadas
    private List<InvitacionDto> invitaciones = new ArrayList<>();
}