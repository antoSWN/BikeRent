package projectprogiii.gestionalesa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Equipaggiamento;
import projectprogiii.gestionalesa.repository.BiciclettaRepository;
import projectprogiii.gestionalesa.repository.EquipaggiamentoRepository;
import projectprogiii.gestionalesa.strategy.CreditCardStrategy;
import projectprogiii.gestionalesa.strategy.IPaymentStrategy;
import projectprogiii.gestionalesa.strategy.PaymentContext;

import java.util.List;

@Service
public class EquipaggiamentoService {

    @Autowired
    private EquipaggiamentoRepository equipRepo;

    // 2. SALVA EQUIPAGGIAMENTO
    public void salvaEquipaggiamento(Equipaggiamento equip) {
        equipRepo.save(equip);
    }

    public void salvaOAggiorna(Equipaggiamento equipInput) {
        // 1. Cerco se esiste già qualcosa con lo stesso nome
        var equipEsistenteOpt = equipRepo.findByNomeIgnoreCase(equipInput.getNome().trim());

        if (equipEsistenteOpt.isPresent()) {
            // CASO A: Esiste già -> AGGIORNO LA QUANTITÀ
            Equipaggiamento equipEsistente = equipEsistenteOpt.get();

            int quantitaVecchia = (equipEsistente.getQuantitaDisponibile() == null) ? 0 : equipEsistente.getQuantitaDisponibile();
            int quantitaNuovaDaAggiungere = (equipInput.getQuantitaDisponibile() == null) ? 0 : equipInput.getQuantitaDisponibile();

            // Sommo: quello che c'era + quello che ha inserito l'admin
            equipEsistente.setQuantitaDisponibile(quantitaVecchia + quantitaNuovaDaAggiungere);

            // (Opzionale) Se vuoi aggiornare anche la descrizione o l'immagine se sono cambiate:
            // equipEsistente.setDescrizione(equipInput.getDescrizione());

            equipRepo.save(equipEsistente);

        } else {
            // CASO B: Non esiste -> CREO NUOVO
            // Mi assicuro che la quantità non sia null
            if (equipInput.getQuantitaDisponibile() == null) {
                equipInput.setQuantitaDisponibile(0);
            }
            equipRepo.save(equipInput);
        }
    }

    public List<Equipaggiamento> findAll() {
        return equipRepo.findAll();
    }
}