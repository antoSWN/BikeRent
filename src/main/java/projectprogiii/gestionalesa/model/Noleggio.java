package projectprogiii.gestionalesa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Noleggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Double kmPercorsi;

    @ManyToMany
    @JoinTable(
            name = "noleggio_equipaggiamento",
            joinColumns = @JoinColumn(name = "noleggio_id"),
            inverseJoinColumns = @JoinColumn(name = "equipaggiamento_id")
    )
    private List<Equipaggiamento> equipaggiamenti;

    // Quale bici?
    @ManyToOne
    @JoinColumn(name = "bicicletta_id")
    private Bicicletta bicicletta;

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine; // Se è null, il noleggio è ancora in corso!

    private double costoTotale;
}