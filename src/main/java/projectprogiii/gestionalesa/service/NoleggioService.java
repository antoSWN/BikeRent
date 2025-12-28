package projectprogiii.gestionalesa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // IMPORTANTE
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
    // 2. INIZIA NOLEGGIO (Factory Method Pattern per le notifiche)
    // -------------------------------------------------------------------------
    @Transactional // Se qualcosa fallisce (es. db down), annulla tutte le modifiche fatte qui dentro
    public void iniziaNoleggio(Long biciId, String username, String tipoNotifica, String recapitoUtente, List<Long> equipaggiamentiIds) {

        Bicicletta bici = biciRepo.findById(biciId)
                .orElseThrow(() -> new RuntimeException("Bicicletta non trovata"));

        if (!bici.isDisponibile()) {
            throw new RuntimeException("La bicicletta risulta già occupata!");
        }

        // 1. Creazione Oggetto Noleggio
        Noleggio noleggio = new Noleggio();
        noleggio.setBicicletta(bici);
        noleggio.setUsername(username);
        noleggio.setDataInizio(LocalDateTime.now());

        // 2. GESTIONE EQUIPAGGIAMENTI E STOCK (Decremento)
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
            noleggio.setEquipaggiamenti(equipScelti);
        }

        // 3. Salvataggio Noleggio
        noleggioRepository.save(noleggio);

        // 4. Aggiornamento Bici (non più disponibile e senza parcheggio)
        bici.setDisponibile(false);
        bici.setParcheggio(null);
        biciRepo.save(bici);

        // 5. INVIO NOTIFICA (Factory Pattern)
        inviaNotifica(tipoNotifica, recapitoUtente, bici.getId());
    }

    // Metodo helper privato per pulire il codice principale
    private void inviaNotifica(String tipoNotifica, String recapito, Long idBici) {
        try {
            NotificaFactory factory;
            if ("SMS".equalsIgnoreCase(tipoNotifica)) {
                factory = new SmsFactory();
            } else {
                factory = new EmailFactory();
            }
            ServizioNotifica servizio = factory.creaNotifica();
            servizio.inviaConferma(recapito, "Noleggio avviato con successo! ID Bici: " + idBici);
        } catch (Exception e) {
            // Logghiamo l'errore ma NON blocchiamo il noleggio se l'email fallisce
            System.err.println("Errore invio notifica: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // 3. TERMINA NOLEGGIO (Strategy Pattern per il pagamento)
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
        bici.setDisponibile(true);
        bici.setParcheggio(parcheggioFinale);
        biciRepo.save(bici);
    }

    // Metodo helper privato per la Strategy
    private void eseguiPagamento(String tipoPagamento, String numeroCarta, double importo) {
        PaymentContext context = new PaymentContext();

        if ("carta".equalsIgnoreCase(tipoPagamento) || "bancomat".equalsIgnoreCase(tipoPagamento)) {
            context.setStrategy(new CreditCardStrategy(numeroCarta));
        } else {
            // Fallback per contanti o altro
            System.out.println("Utente paga in contanti o metodo non tracciato.");
            // Usiamo una strategia dummy o logghiamo solamente
            context.setStrategy(new CreditCardStrategy("CONTANTI"));
        }

        context.executePayment(importo);
    }
}