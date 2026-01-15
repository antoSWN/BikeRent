package projectprogiii.gestionalesa.state;

import projectprogiii.gestionalesa.model.Bicicletta;

// State
public interface StatoBicicletta {
    void noleggia(Bicicletta bici);
    void restituisci(Bicicletta bici);
    String getStatoAsString();
}
