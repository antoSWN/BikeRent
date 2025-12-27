package projectprogiii.gestionalesa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Equipaggiamento;
import projectprogiii.gestionalesa.repository.BiciclettaRepository;
import projectprogiii.gestionalesa.repository.EquipaggiamentoRepository;
import projectprogiii.gestionalesa.strategy.*; // Importa le tue strategie

import java.util.List;

@Service
public class BiciclettaService {

    @Autowired
    private BiciclettaRepository biciclettaRepository;

    // Aggiungi questo metodo che mancava:
    public void salvaBicicletta(Bicicletta bici) {
        biciclettaRepository.save(bici);
    }

    public Bicicletta getBiciclettaById(Long id) {
        return biciclettaRepository.findById(id).orElse(null);
    }

    // 3. AGGIORNA TARIFFA
    // Cerchiamo la bici per ID, cambiamo il prezzo e risalviamo
    public void aggiornaTariffa(Long idBici, double nuovaTariffa) {
        Bicicletta bici = biciclettaRepository.findById(idBici).orElse(null);
        if (bici != null) {
            bici.setTariffaOraria(nuovaTariffa);
            biciclettaRepository.save(bici);
        }
    }

    // 4. STATISTICHE (Logica semplificata)
    // Ritorna tutte le bici per calcolare le percentuali nella view
    public List<Bicicletta> getTutteLeBici() {
        return biciclettaRepository.findAll();
    }

}