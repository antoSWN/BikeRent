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

    // La stringa statoDB conserva l'informazione precisa, non binaria.
    // Aggiunto per estensibilità, siccome booleano accetta solo 2 valori...
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

    // --- STATE PATTERN ---
    // rappresenta "-current" nel diagramma UML State del prof
    @Transient // Non salvato nel DB, vive solo in memoria RAM
    private StatoBicicletta statoCorrente;

    /*
        il setter setStatoCorrente() non è visibile perchè generato da Lombok.
        Ed è il metodo usato per cambiare lo stato
    */

    // Quando Hibernate carica la bici dal DB, inizializza lo Stato corretto --> reidratazione oggetto
    @PostLoad
    private void initStato() {
        if ("NOLEGGIATA".equals(statoDB)) {
            this.statoCorrente = new StatoNoleggiata();
        } else {
            this.statoCorrente = new StatoDisponibile();
        }
    }

    // Metodi che il Service chiamerà

    // equivale al metodo goNext()
    public void tentaNoleggio() {
        if (statoCorrente == null) initStato();
        statoCorrente.noleggia(this); // Delega allo stato
        this.statoDB = statoCorrente.getStatoAsString(); // Aggiorno stringa per DB
    }

    // equivale al metodo goNext()
    public void tentaRestituzione() {
        if (statoCorrente == null) initStato();
        statoCorrente.restituisci(this); // Delega allo stato
        this.statoDB = statoCorrente.getStatoAsString(); // Aggiorno stringa per DB
    }

    /*

        NOTE STATE PATTERN : triangolo oggetti

            StatoBicicletta statoCorrente, è un oggetto che ha metodi, non è un campo semplice.
            Quindi ovviamente non va salvato nel db, ecco perchè aggiungiamo @Transient.

            Siccome è transient, ogni volta che recupero l'entità dal db, la colonna non esiste,
            va popolata ogni volta. Però è un oggetto complesso con dei metodi, quindi dobbiamo
            istanziare l'oggetto in base al valore di "disponibile"; questo si fa tramite l'annotazione
            @PostLoad. Quello che si fa in maniera tecnica è la reidratazione dell'oggetot.s

            boolean disponibile, è utile per questioni di querying
                - es. SELECT * FROM Bici WHERE disponibile = 1

            String statoDB, è utile in caso aggiungiamo più stati, es. "Manutenzione"

     */

}