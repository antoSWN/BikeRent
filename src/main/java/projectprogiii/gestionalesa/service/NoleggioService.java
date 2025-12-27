package projectprogiii.gestionalesa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Equipaggiamento;
import projectprogiii.gestionalesa.model.Noleggio;
import projectprogiii.gestionalesa.model.Parcheggio;
import projectprogiii.gestionalesa.pattern.NotificaFactory;
import projectprogiii.gestionalesa.pattern.ServizioNotifica;
import projectprogiii.gestionalesa.pattern.factories.EmailFactory;
import projectprogiii.gestionalesa.pattern.factories.SmsFactory;
import projectprogiii.gestionalesa.repository.BiciclettaRepository;
import projectprogiii.gestionalesa.repository.EquipaggiamentoRepository;
import projectprogiii.gestionalesa.repository.NoleggioRepository;
import projectprogiii.gestionalesa.repository.ParcheggioRepository;
import projectprogiii.gestionalesa.strategy.*; // Le tue strategie

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoleggioService {

    @Autowired
    private NoleggioRepository noleggioRepository;

    @Autowired
    private BiciclettaRepository biciRepo;

    @Autowired
    private ParcheggioRepository parcheggioRepo;

    @Autowired
    private EquipaggiamentoRepository equipRepo;

    public List<Noleggio> findNoleggiAttivi(String username) {
        return noleggioRepository.findByUsernameAndDataFineIsNull(username);
    }

    public Noleggio findById(Long id) {
        return noleggioRepository.findById(id).orElse(null);
    }

    // 2. INIZIA NOLEGGIO (Factory Method Pattern per le notifiche)
    public void iniziaNoleggio(
            Long biciId, String username, String tipoNotifica, String recapitoUtente, List<Long> equipaggiamentiIds
    ) {
        Bicicletta bici = biciRepo.findById(biciId).orElse(null);

        if (bici != null && bici.isDisponibile()) {

            // Crea il nuovo noleggio
            Noleggio noleggio = new Noleggio();
            noleggio.setBicicletta(bici);
            noleggio.setUsername(username);
            noleggio.setDataInizio(LocalDateTime.now());

            // --- GESTIONE EQUIPAGGIAMENTO ---
            if (equipaggiamentiIds != null && !equipaggiamentiIds.isEmpty()) {
                List<Equipaggiamento> equipScelti = equipRepo.findAllById(equipaggiamentiIds);
                noleggio.setEquipaggiamenti(equipScelti);
            }

            noleggioRepository.save(noleggio);

            // Segna la bici come occupata e toglie il parcheggio (è in viaggio)
            bici.setDisponibile(false);
            bici.setParcheggio(null);
            biciRepo.save(bici);

            // --- FACTORY METHOD PATTERN ---
            NotificaFactory factory;
            if ("SMS".equalsIgnoreCase(tipoNotifica)) {
                factory = new SmsFactory();
            } else {
                factory = new EmailFactory();
            }

            ServizioNotifica servizio = factory.creaNotifica();
            servizio.inviaConferma(recapitoUtente, "Noleggio avviato con successo! ID Bici: " + bici.getId());
        }
    }

    // 3. TERMINA NOLEGGIO (Strategy Pattern per il pagamento)
    // Ora accetta idNoleggio per essere precisi e sicuri
    public void terminaNoleggio(Long idNoleggio, String username, Long parcheggioId, String tipoPagamento, String numeroCarta, Double kmPercorsi) {

        Noleggio noleggio = findById(idNoleggio);
        Parcheggio parcheggioFinale = parcheggioRepo.findById(parcheggioId).orElse(null);

        if (noleggio != null && parcheggioFinale != null && noleggio.getUsername().equals(username)) {

            // 1. Calcolo Importo basato sui KM (non più sul tempo)
            double tariffa = noleggio.getBicicletta().getTariffaOraria(); // Usiamo questo campo come tariffa/km
            double importo = kmPercorsi * tariffa;

            // 2. Strategy Pagamento
            PaymentContext context = new PaymentContext();

            // Gestione Carta e Bancomat
            if ("carta".equalsIgnoreCase(tipoPagamento) || "bancomat".equalsIgnoreCase(tipoPagamento)) {
                context.setStrategy(new CreditCardStrategy(numeroCarta));
            } else {
                // Gestione Contanti
                // Qui potresti fare una classe 'CashStrategy' o stampare solo un log
                System.out.println("Utente paga in contanti alla cassa automatica.");
                context.setStrategy(new CreditCardStrategy("CONTANTI")); // Fallback per non rompere il codice
            }

            context.executePayment(importo);

            // 3. Salvataggio dati finali
            noleggio.setDataFine(LocalDateTime.now());
            noleggio.setKmPercorsi(kmPercorsi); // SALVIAMO I KM
            noleggio.setCostoTotale(importo);
            noleggioRepository.save(noleggio);

            // 4. Riposizionamento Bici
            Bicicletta bici = noleggio.getBicicletta();
            bici.setDisponibile(true);
            bici.setParcheggio(parcheggioFinale);
            biciRepo.save(bici);
        }
    }
}