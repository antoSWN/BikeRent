package projectprogiii.gestionalesa.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // IMPORTANTE PER GLI ERRORI
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.repository.EquipaggiamentoRepository;
import projectprogiii.gestionalesa.service.BiciclettaService;
import projectprogiii.gestionalesa.service.NoleggioService;

import java.util.List;

@Controller
@RequestMapping("/noleggio")
public class NoleggioController {

    @Autowired
    private BiciclettaService biciService;

    // Se usi la repo direttamente va bene, altrimenti usa il service se ha un metodo findAll()
    @Autowired
    private EquipaggiamentoRepository equipRepo;

    @Autowired
    private NoleggioService noleggioService;

    // 1. MOSTRA FORM DI CONFERMA
    @GetMapping("/prenota/{idBici}")
    public String mostraConfermaPrenotazione(@PathVariable Long idBici,
                                             HttpSession session,
                                             Model model,
                                             @RequestParam(required = false) String error) { // Gestione errore URL

        // A. Controllo Login
        if (session.getAttribute("utenteLoggato") == null) {
            return "redirect:/login";
        }

        // B. Recupero dati
        Bicicletta bici = biciService.getBiciclettaById(idBici);

        // C. Controllo Validità
        if (bici == null || !bici.isDisponibile()) {
            return "redirect:/catalogo?error=nonDisponibile";
        }

        // D. Passaggio dati alla View
        model.addAttribute("listaEquipaggiamenti", equipRepo.findAll());
        model.addAttribute("bici", bici);

        // E. Se c'è un errore (es. arrivato dal catch sotto), lo mostriamo
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }

        return "Client/ConfermaPrenotazione";
    }

    // 2. AVVIA IL NOLEGGIO EFFETTIVO
    @PostMapping("/avvia")
    public String avviaNoleggio(
            @RequestParam Long idBici,
            @RequestParam String tipoNotifica,
            @RequestParam String recapito,
            @RequestParam(required = false) List<Long> equipaggiamentiIds,
            HttpSession session,
            RedirectAttributes redirectAttributes // Per passare messaggi dopo il redirect
    ) {

        String username = (String) session.getAttribute("utenteLoggato");
        if (username == null) return "redirect:/login";

        try {
            // Tenta di avviare il noleggio
            noleggioService.iniziaNoleggio(idBici, username, tipoNotifica, recapito, equipaggiamentiIds);

            // Successo: redirect alla home con messaggio verde
            redirectAttributes.addFlashAttribute("successMessage", "Noleggio avviato! Buona pedalata.");
            return "redirect:/client/home";

        } catch (RuntimeException e) {
            // ERRORE (es. Casco finito mentre cliccavi): Ritorna alla pagina di prenotazione
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Torniamo alla pagina di conferma specifica per quella bici
            return "redirect:/noleggio/prenota/" + idBici;
        }
    }

    // 3. CHIUDI NOLEGGIO
    @PostMapping("/chiudi")
    public String terminaNoleggio(
            @RequestParam Long idNoleggio,
            @RequestParam Long idParcheggio,
            @RequestParam String numeroCarta,
            @RequestParam String metodoPagamento,
            @RequestParam Double kmPercorsi,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        String username = (String) session.getAttribute("utenteLoggato");
        if (username == null) return "redirect:/login";

        try {
            noleggioService.terminaNoleggio(idNoleggio, username, idParcheggio, metodoPagamento, numeroCarta, kmPercorsi);

            redirectAttributes.addFlashAttribute("successMessage", "Noleggio terminato e pagato con successo!");
            return "redirect:/client/home";

        } catch (Exception e) {
            // In caso di errore nel pagamento o chiusura
            redirectAttributes.addFlashAttribute("errorMessage", "Errore chiusura noleggio: " + e.getMessage());
            return "redirect:/client/home";
        }
    }
}