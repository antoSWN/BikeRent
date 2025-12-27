package projectprogiii.gestionalesa.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.repository.EquipaggiamentoRepository;
import projectprogiii.gestionalesa.service.BiciclettaService;
import projectprogiii.gestionalesa.service.EquipaggiamentoService;
import projectprogiii.gestionalesa.service.NoleggioService;

import java.util.List;

@Controller
@RequestMapping("/noleggio")
public class NoleggioController {

    @Autowired
    private BiciclettaService biciService;

    @Autowired
    private EquipaggiamentoService equipService;

    @Autowired
    private NoleggioService noleggioService;

    @Autowired
    private EquipaggiamentoRepository equipRepo;

    // 1. MOSTRA FORM DI CONFERMA (Arriva dal click su "Prenota" nel catalogo)
    @GetMapping("/prenota/{idBici}")
    public String mostraConfermaPrenotazione(@PathVariable Long idBici,
                                             HttpSession session,
                                             Model model) {

        // A. Controllo Sicurezza: L'utente è loggato?
        if (session.getAttribute("utenteLoggato") == null) {
            return "redirect:/login"; // Se non loggato, va al login
        }

        // B. Recupero la bici
        Bicicletta bici = biciService.getBiciclettaById(idBici);

        // C. Controllo se esiste ed è disponibile
        if (bici == null || !bici.isDisponibile()) {
            return "redirect:/catalogo?error=nonDisponibile";
        }

        // D. Passo i dati alla view
        model.addAttribute("listaEquipaggiamenti", equipRepo.findAll());
        model.addAttribute("bici", bici);

        return "Client/ConfermaPrenotazione"; // Creeremo questa view tra poco
    }

    // 2. AVVIA IL NOLEGGIO EFFETTIVO (Click su "Conferma e Parti")
    @PostMapping("/avvia")
    public String avviaNoleggio(
            @RequestParam Long idBici,
            @RequestParam String tipoNotifica, // SMS o EMAIL
            @RequestParam String recapito,
            @RequestParam(required = false) List<Long> equipaggiamentiIds,
            HttpSession session
    ) {

        String username = (String) session.getAttribute("utenteLoggato");

        if (username == null) return "redirect:/login";

        // Chiama il service per creare il record nel DB e occupare la bici
        noleggioService.iniziaNoleggio(idBici, username, tipoNotifica, recapito, equipaggiamentiIds);

        // Rimanda alla Dashboard utente dove vedrà il "Noleggio in Corso"
        return "redirect:/client/home";
    }

    @PostMapping("/chiudi")
    public String terminaNoleggio(
            @RequestParam Long idNoleggio,
            @RequestParam Long idParcheggio,
            @RequestParam String numeroCarta,
            @RequestParam String metodoPagamento,
            @RequestParam Double kmPercorsi,
            HttpSession session
    ) {

        String username = (String) session.getAttribute("utenteLoggato");
        if (username == null) return "redirect:/login";

        // Chiama il service per chiudere e pagare
        noleggioService.terminaNoleggio(idNoleggio, username, idParcheggio, metodoPagamento, numeroCarta, kmPercorsi);

        return "redirect:/client/home"; // O una pagina di conferma
    }
}