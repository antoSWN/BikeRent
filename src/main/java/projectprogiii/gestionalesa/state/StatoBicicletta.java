package projectprogiii.gestionalesa.state;

import projectprogiii.gestionalesa.model.Bicicletta;

public interface StatoBicicletta {
    void noleggia(Bicicletta bici);
    void restituisci(Bicicletta bici);
    String getStatoAsString();
}
