package projectprogiii.gestionalesa.builder;

import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Equipaggiamento;
import java.time.LocalDateTime;
import java.util.List;

// Concrete Builder, ASCIIConverter
public class StandardNoleggioBuilder extends NoleggioBuilder {

    @Override
    public void buildCliente(String username) {
        System.out.println("BUILDER: Imposto il cliente " + username);
        noleggio.setUsername(username);
    }

    @Override
    public void buildBicicletta(Bicicletta bici) {
        System.out.println("BUILDER: Associo la bici " + bici.getModello());
        noleggio.setBicicletta(bici);
    }

    @Override
    public void buildEquipaggiamenti(List<Equipaggiamento> equipaggiamenti) {
        if (equipaggiamenti != null && !equipaggiamenti.isEmpty()) {
            System.out.println("BUILDER: Aggiungo " + equipaggiamenti.size() + " accessori.");
            noleggio.setEquipaggiamenti(equipaggiamenti);
        }
    }

    @Override
    public void buildDataInizio() {
        noleggio.setDataInizio(LocalDateTime.now());
    }
}
