package projectprogiii.gestionalesa.state;

import projectprogiii.gestionalesa.model.Bicicletta;

// StateOne
public class StatoDisponibile implements StatoBicicletta {
    @Override
    public void noleggia(Bicicletta bici) {
        // Transizione valida: Cambio stato e setto il flag booleano
        System.out.println("Bici noleggiata con successo.");
        bici.setStatoCorrente(new StatoNoleggiata()); // context.setState(StateTwo)
        bici.setDisponibile(false);
    }

    @Override
    public void restituisci(Bicicletta bici) {
        // Operazione illegale in questo stato
        throw new RuntimeException("Errore: La bici è già nel parcheggio!");
    }

    @Override
    public String getStatoAsString() { return "DISPONIBILE"; }
}
