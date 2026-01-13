package projectprogiii.gestionalesa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import projectprogiii.gestionalesa.state.StatoBicicletta;
import projectprogiii.gestionalesa.state.StatoDisponibile;
import projectprogiii.gestionalesa.state.StatoNoleggiata;

@Entity
@Data
@NoArgsConstructor // <--- Genera il costruttore vuoto obbligatorio per JPA
public class Bicicletta {

    @Id // Chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (1, 2, 3...)
    private Long id;

    private String modello;      // Es. "Bianchi X500"
    private String categoria;    // Es. "Corsa", "Passeggio" (Qui useremo il pattern Factory)
    private double tariffaOraria;
    private boolean disponibile; // True = in parcheggio, False = noleggiata, Gestito da State
    private int numeroUtilizzi; // utile per le statistiche

    // Questo campo serve solo a salvare lo stato come stringa nel DB ("DISPONIBILE", "NOLEGGIATA")
    private String statoDB;

    // Una biciletta risiede in un solo parcheggio
    @ManyToOne
    @JoinColumn(name = "parcheggio_id")
    private Parcheggio parcheggio;

    // --- COSTRUTTORE ---
    public Bicicletta(String modello, String categoria, double tariffaOraria, int numeroUtilizzi) {
        this.modello = modello;
        this.categoria = categoria;
        this.tariffaOraria = tariffaOraria;
        this.numeroUtilizzi = numeroUtilizzi;
        this.disponibile = true; // Appena creata è disponibile
        this.statoCorrente = new StatoDisponibile();
        this.statoDB = "DISPONIBILE";
    }

    // --- STATE PATTERN (Non salvato nel DB, vive solo in memoria RAM) ---
    @Transient
    private StatoBicicletta statoCorrente;

    // Quando Hibernate carica la bici dal DB, inizializza lo Stato corretto
    @PostLoad
    private void initStato() {
        if ("NOLEGGIATA".equals(statoDB)) {
            this.statoCorrente = new StatoNoleggiata();
        } else {
            this.statoCorrente = new StatoDisponibile();
        }
    }

    // Metodi che il Service chiamerà
    public void tentaNoleggio() {
        if (statoCorrente == null) initStato();
        statoCorrente.noleggia(this); // Delega allo stato
        this.statoDB = statoCorrente.getStatoAsString(); // Aggiorno stringa per DB
    }

    public void tentaRestituzione() {
        if (statoCorrente == null) initStato();
        statoCorrente.restituisci(this); // Delega allo stato
        this.statoDB = statoCorrente.getStatoAsString(); // Aggiorno stringa per DB
    }

}