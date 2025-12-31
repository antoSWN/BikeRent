package projectprogiii.gestionalesa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "L'username non può essere vuoto")
    private String username;
    private Double kmPercorsi; // se non abbiamo percorso km, sarà null, non 0.0.

    @ManyToMany
    @JoinTable(
            name = "noleggio_equipaggiamento",
            joinColumns = @JoinColumn(name = "noleggio_id"),
            inverseJoinColumns = @JoinColumn(name = "equipaggiamento_id")
    )
    private List<Equipaggiamento> equipaggiamenti; // possiamo anche non avere equipaggiamenti !

    // Quale bici?
    @ManyToOne
    @JoinColumn(name = "bicicletta_id")
    private Bicicletta bicicletta;

    private LocalDateTime dataInizio;
    private LocalDateTime dataFine; // Se è null, il noleggio è ancora in corso!

    // Qua è double non Double
    private double costoTotale; // qua non parliamo di null, ma 0.0
}