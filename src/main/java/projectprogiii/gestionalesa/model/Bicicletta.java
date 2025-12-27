package projectprogiii.gestionalesa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor // <--- Genera il costruttore vuoto obbligatorio per JPA
public class Bicicletta {

    @Id // Chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (1, 2, 3...)
    private Long id;

    private String modello;      // Es. "Bianchi X500"
    private String categoria;    // Es. "Corsa", "Passeggio" (Qui useremo la Factory dopo!)
    private double tariffaOraria;
    private boolean disponibile; // True = in parcheggio, False = noleggiata
    private int numeroUtilizzi;

    @ManyToOne
    @JoinColumn(name = "parcheggio_id")
    private Parcheggio parcheggio;

    // --- COSTRUTTORI ---

    public Bicicletta(String modello, String categoria, double tariffaOraria, int numeroUtilizzi) {
        this.modello = modello;
        this.categoria = categoria;
        this.tariffaOraria = tariffaOraria;
        this.numeroUtilizzi = numeroUtilizzi;
        this.disponibile = true; // Appena creata è disponibile
    }


}