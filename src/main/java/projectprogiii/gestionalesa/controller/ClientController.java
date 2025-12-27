package projectprogiii.gestionalesa.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Noleggio;
import projectprogiii.gestionalesa.repository.ParcheggioRepository;
import projectprogiii.gestionalesa.service.BiciclettaService;
import projectprogiii.gestionalesa.service.NoleggioService;

import java.util.List;

@Controller
@RequestMapping("/client") // Tutte le pagine qui inizieranno con /client/...
public class ClientController {

    @Autowired
    private BiciclettaService biciclettaService;

    @Autowired
    private NoleggioService noleggioService;

    @Autowired
    private ParcheggioRepository parcheggioRepository;

    @GetMapping("/home")
    public String showClientHome(Model model, HttpSession session) {
        String username = (String) session.getAttribute("utenteLoggato");

        List<Noleggio> noleggiAttivi = noleggioService.findNoleggiAttivi(username);

        model.addAttribute("noleggiAttivi", noleggiAttivi);

        return "Client/User_home";
    }

    // MOSTRA LA PAGINA DI CHIUSURA
    @GetMapping("/termina-corsa/{idNoleggio}")
    public String mostraTerminaCorsa(@PathVariable Long idNoleggio, HttpSession session, Model model) {
        String username = (String) session.getAttribute("utenteLoggato");

        // Recuperiamo QUEL noleggio specifico per mostrarne i dettagli (costo parziale, bici, ecc)
        Noleggio noleggio = noleggioService.findById(idNoleggio); // Da creare nel service se non c'è

        // Controllo sicurezza: è davvero suo?
        if (noleggio == null || !noleggio.getUsername().equals(username)) {
            return "redirect:/client/home";
        }

        // 2. CALCOLO PREZZO STIMATO
        // --- SIMULAZIONE KM E CALCOLO COSTO ---

        // 1. Genera Km casuali tra 1.0 e 15.0
        double kmSimulati = 1.0 + (Math.random() * 14.0);

        // 2. Recupera la tariffa della bici (ipotizziamo sia Euro al Km ora)
        double tariffaPerKm = noleggio.getBicicletta().getTariffaOraria(); // Usiamo questo campo come tariffa km

        // 3. Calcola importo
        double importoDaPagare = kmSimulati * tariffaPerKm;

        // Formattiamo per avere solo 2 decimali (es. "4.50")
        model.addAttribute("kmFatti", String.format("%.1f", kmSimulati).replace(',', '.'));
        model.addAttribute("importoStimato", String.format("%.2f", importoDaPagare).replace(',', '.'));

        model.addAttribute("noleggio", noleggio);
        model.addAttribute("listaParcheggi", parcheggioRepository.findAll());

        return "Client/TerminaCorsa";
    }

}