package projectprogiii.gestionalesa.state;

import projectprogiii.gestionalesa.model.Bicicletta;

// StateTwo
public class StatoNoleggiata implements StatoBicicletta {
    @Override
    public void noleggia(Bicicletta bici) {
        // Operazione illegale
        throw new RuntimeException("Errore: La bici è già occupata!");
    }

    @Override
    public void restituisci(Bicicletta bici) {
        // Transizione valida
        System.out.println("Bici restituita.");
        bici.setStatoCorrente(new StatoDisponibile());
        bici.setDisponibile(true);
    }

    @Override
    public String getStatoAsString() { return "NOLEGGIATA"; }
}
