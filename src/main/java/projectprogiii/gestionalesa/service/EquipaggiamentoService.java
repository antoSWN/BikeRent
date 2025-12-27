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

    public List<Equipaggiamento> findAll() {
        return equipRepo.findAll();
    }
}