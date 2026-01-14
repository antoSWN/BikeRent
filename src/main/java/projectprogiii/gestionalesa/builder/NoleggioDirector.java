package projectprogiii.gestionalesa.builder;

import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Equipaggiamento;
import java.util.List;

// Director, RTFReader
public class NoleggioDirector {

    private NoleggioBuilder builder;

    // Iniezione del Builder (come nel costruttore RTFReader(TextConverter obj))
    public NoleggioDirector(NoleggioBuilder builder) {
        this.builder = builder;
    }

    // Metodo "costruisci" (corrisponde a parseRTF)
    // Prende i dati e orchestra la costruzione
    public void costruisciNoleggio(String username, Bicicletta bici, List<Equipaggiamento> equipaggiamenti) {
        builder.createNewNoleggioInstance();
        builder.buildCliente(username);
        builder.buildBicicletta(bici);
        builder.buildEquipaggiamenti(equipaggiamenti);
        builder.buildDataInizio();
    }
}
