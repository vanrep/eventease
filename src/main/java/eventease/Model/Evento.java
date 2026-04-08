package eventease.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter    @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;
 
    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidad;

    @ManyToOne(optional = false)
    private Usuario cliente;

    @OneToMany(mappedBy = "evento")
    private List<Invitacion> invitaciones = new ArrayList<>();

    // @OneToMany
    // private List<Usuario> asistentes;
    // Esto no puede ser de tipo List<Usuario.role.ASISTENTE> porque no es un tipo, es un valor del enum.
    // Usuario.role es un enum de tipo string, entonces si quisieras tener una lista el tipo sería List<string>.
    // En este caso los asistentes son un tipo de Usuario, entonces tu lista es de Usuarios, pero todos los usuarios 
    // de esa lista tendrán tipo "ASISTENTE".
}
