package projectprogiii.gestionalesa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // IMPORTANTE
import projectprogiii.gestionalesa.builder.NoleggioBuilder;
import projectprogiii.gestionalesa.builder.NoleggioDirector;
import projectprogiii.gestionalesa.builder.StandardNoleggioBuilder;
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
import projectprogiii.gestionalesa.strategy.CashStrategy;
import projectprogiii.gestionalesa.strategy.CreditCardStrategy;
import projectprogiii.gestionalesa.strategy.PaymentContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // -------------------------------------------------------------------------
    // 2. INIZIA NOLEGGIO (Factory Method + Builder Pattern)
    // -------------------------------------------------------------------------
    @Transactional // Se qualcosa fallisce (es. db down), annulla tutte le modifiche fatte qui dentro
    public void iniziaNoleggio(Long biciId, String username, String tipoNotifica, String recapitoUtente, List<Long> equipaggiamentiIds) {

        Bicicletta bici = biciRepo.findById(biciId)
                .orElseThrow(() -> new RuntimeException("Bicicletta non trovata"));

        // --- [STATE PATTERN] ---
        // Non controlliamo più manualmente "if (!bici.isDisponibile)".
        // Chiamiamo il metodo che delega allo Stato Corrente.
        // Se la bici è già occupata, questo metodo lancerà RuntimeException da solo.
        bici.tentaNoleggio();

        // GESTIONE EQUIPAGGIAMENTI E STOCK (Decremento)
        List<Equipaggiamento> equipScelti = new ArrayList<>();

        if (equipaggiamentiIds != null && !equipaggiamentiIds.isEmpty()) {
            for (Long idEq : equipaggiamentiIds) {
                Equipaggiamento eq = equipRepo.findById(idEq)
                        .orElseThrow(() -> new RuntimeException("Equipaggiamento ID " + idEq + " non trovato"));

                // Controllo se c'è disponibilità
                int qtyAttuale = (eq.getQuantitaDisponibile() == null) ? 0 : eq.getQuantitaDisponibile();

                if (qtyAttuale > 0) {
                    // Scalo la quantità
                    eq.setQuantitaDisponibile(qtyAttuale - 1);
                    equipRepo.save(eq); // Salvo aggiornamento magazzino

                    equipScelti.add(eq);
                } else {
                    // Se un utente ha provato a hackerare il form o qualcuno l'ha preso un secondo prima
                    throw new RuntimeException("L'articolo '" + eq.getNome() + "' è appena terminato!");
                }
            }
        }

        // --- [BUILDER PATTERN GoF] ---

        // Istanzio il ConcreteBuilder
        NoleggioBuilder builder = new StandardNoleggioBuilder();

        // Istanzio il Director passandogli il builder
        NoleggioDirector director = new NoleggioDirector(builder);

        // Il Director costruisce l'oggetto passo dopo passo
        director.costruisciNoleggio(username, bici, equipScelti);

        // Recupero il prodotto finito dal Builder
        Noleggio noleggio = builder.getNoleggio();

        // Salvataggio Noleggio
        noleggioRepository.save(noleggio);

        // Aggiornamento Bici (non più disponibile e senza parcheggio)
        bici.setDisponibile(false);
        bici.setParcheggio(null);
        biciRepo.save(bici); // Questo salverà anche la stringa "NOLEGGIATA" nel DB

        // INVIO NOTIFICA (Factory Pattern)
        inviaNotifica(tipoNotifica, recapitoUtente, bici.getId());
    }

    private void inviaNotifica(String tipoNotifica, String recapito, Long idBici) {
        try {
            NotificaFactory factory;
            if ("SMS".equalsIgnoreCase(tipoNotifica)) {
                factory = new SmsFactory();
            } else {
                factory = new EmailFactory();
            }

            // Chiamo il metodo della classe astratta
            factory.mandaNotifica(recapito, "Noleggio avviato con successo! ID Bici: " + idBici);
        } catch (Exception e) {
            // Logghiamo l'errore ma NON blocchiamo il noleggio se l'email fallisce
            System.err.println("Errore invio notifica: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // 3. TERMINA NOLEGGIO (Strategy Pattern + STATE Pattern)
    // -------------------------------------------------------------------------
    @Transactional // Fondamentale per garantire il ripristino dello stock
    public void terminaNoleggio(Long idNoleggio, String username, Long parcheggioId, String tipoPagamento, String numeroCarta, Double kmPercorsi) {

        Noleggio noleggio = findById(idNoleggio);
        if (noleggio == null) throw new RuntimeException("Noleggio non trovato");
        if (!noleggio.getUsername().equals(username)) throw new RuntimeException("Utente non autorizzato");

        Parcheggio parcheggioFinale = parcheggioRepo.findById(parcheggioId)
                .orElseThrow(() -> new RuntimeException("Parcheggio destinazione non valido"));

        // 1. RIPRISTINO STOCK EQUIPAGGIAMENTI (Incremento)
        List<Equipaggiamento> equipUsati = noleggio.getEquipaggiamenti();
        if (equipUsati != null) {
            for (Equipaggiamento eq : equipUsati) {
                int qty = (eq.getQuantitaDisponibile() == null) ? 0 : eq.getQuantitaDisponibile();
                eq.setQuantitaDisponibile(qty + 1);
                equipRepo.save(eq);
            }
        }

        // 2. Calcolo Importo (Strategy Preparation)
        double tariffa = noleggio.getBicicletta().getTariffaOraria(); // Assumiamo tariffa/km come da tua logica precedente
        double importo = kmPercorsi * tariffa;

        // 3. Esecuzione Pagamento (Strategy Pattern)
        eseguiPagamento(tipoPagamento, numeroCarta, importo);

        // 4. Salvataggio dati finali del noleggio
        noleggio.setDataFine(LocalDateTime.now());
        noleggio.setKmPercorsi(kmPercorsi);
        noleggio.setCostoTotale(importo);
        noleggioRepository.save(noleggio);

        // 5. Riposizionamento Bici nel nuovo parcheggio
        Bicicletta bici = noleggio.getBicicletta();

        // --- [STATE PATTERN] ---
        // Deleghiamo allo stato corrente (che è Noleggiata) il compito di "tornare disponibile".
        // Questo imposterà internamente lo stato a Disponibile e il flag booleano a true.
        bici.tentaRestituzione();

        // bici.setDisponibile(true); NON PIU !!!! STATE RISOLVE.
        bici.setParcheggio(parcheggioFinale);
        biciRepo.save(bici); // Salverà la stringa "DISPONIBILE" nel DB
    }

    // Metodo helper privato per la Strategy
    private void eseguiPagamento(String tipoPagamento, String numeroCarta, double importo) {
        PaymentContext context = new PaymentContext();

        // equalsIgnoreCase() confronta due stringhe ignorando completamente la differenza tra maiuscole e minuscole
        if ("carta".equalsIgnoreCase(tipoPagamento) || "bancomat".equalsIgnoreCase(tipoPagamento)) {
            // Carta o Bancomat
            System.out.println("Utente paga in digitale.");
            context.setStrategy(new CreditCardStrategy(numeroCarta));
        } else {
            // Contanti
            System.out.println("Utente paga in contanti.");
            context.setStrategy(new CashStrategy());
        }

        context.executePayment(importo);
    }
}