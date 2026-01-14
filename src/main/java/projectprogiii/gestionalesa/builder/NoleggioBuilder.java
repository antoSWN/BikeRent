package projectprogiii.gestionalesa.builder;

import projectprogiii.gestionalesa.model.*;
import java.util.List;

// Abstract Builder, TextConverter
public abstract class NoleggioBuilder {

    // Riferimento al Product
    protected Noleggio noleggio;

    // Metodo per inizializzare il prodotto vuoto
    public void createNewNoleggioInstance() {
        this.noleggio = new Noleggio();
    }

    // Step astratti di costruzione
    public abstract void buildCliente(String username);
    public abstract void buildBicicletta(Bicicletta bici);
    public abstract void buildEquipaggiamenti(List<Equipaggiamento> equipaggiamenti);
    public abstract void buildDataInizio();

    // Metodo per ottenere il risultato, banalmente un Getter
    public Noleggio getNoleggio() {
        return noleggio;
    }
}
