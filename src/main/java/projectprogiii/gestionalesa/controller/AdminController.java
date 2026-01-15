package projectprogiii.gestionalesa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Importante per i messaggi
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Equipaggiamento;
import projectprogiii.gestionalesa.service.BiciclettaService;
import projectprogiii.gestionalesa.service.EquipaggiamentoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BiciclettaService biciService;

    @Autowired
    private EquipaggiamentoService equipService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        // 1. Recupera Equipaggiamenti (con il fix anti-null)
        List<Equipaggiamento> equipList = equipService.findAll();
        if (equipList == null) equipList = new ArrayList<>();
        model.addAttribute("listaEquipaggiamenti", equipList);

        // 2. Recupera Bici
        List<Bicicletta> listaBici = biciService.getTutteLeBici();
        if (listaBici == null) listaBici = new ArrayList<>();
        model.addAttribute("listaBici", listaBici);

        // --- CALCOLI STATISTICI PER LA DASHBOARD ---

        // A. Calcolo il TOTALE assoluto dei noleggi (serve per le percentuali)
        int totaleNoleggi = 0;
        for (Bicicletta b : listaBici) {
            totaleNoleggi += b.getNumeroUtilizzi();
        }
        model.addAttribute("totaleNoleggi", totaleNoleggi);

        // B. Raggruppo per Categoria (Mappa: NomeCategoria -> NumeroUtilizzi)
        Map<String, Integer> statsCategorie = new HashMap<>();
        for (Bicicletta b : listaBici) {
            String cat = b.getCategoria();
            // Se la categoria esiste già, somma il valore, altrimenti metti il nuovo valore
            statsCategorie.put(cat, statsCategorie.getOrDefault(cat, 0) + b.getNumeroUtilizzi());
        }
        model.addAttribute("statsCategorie", statsCategorie);

        return "Admin/Admin_dashboard";
    }

    // ---------------------------------------------------------
    // 1. GESTIONE BICICLETTE
    // ---------------------------------------------------------
    @GetMapping("/add-bike")
    public String showAddBikeForm(Model model) {
        model.addAttribute("nuovaBici", new Bicicletta());
        return "Admin/CreateBike";
    }

    @PostMapping("/add-bike")
    public String saveBike(@ModelAttribute Bicicletta bici, RedirectAttributes redirectAttributes) {
        // 1. Impostiamo la bici come disponibile
        bici.setDisponibile(true);

        // 2. Impostiamo lo stato stringa per il DB (fondamentale per lo State Pattern!)
        bici.setStatoDB("DISPONIBILE");

        // 3. Inizializziamo il contatore utilizzi a 0
        bici.setNumeroUtilizzi(0);

        biciService.salvaBicicletta(bici);
        redirectAttributes.addFlashAttribute("successMessage", "Bicicletta aggiunta con successo!");
        return "redirect:/admin/dashboard";
    }

    // --- 3. AGGIORNARE TARIFFE ---
    @GetMapping("/update-tariffs")
    public String mostraTariffe(Model model) {
        model.addAttribute("listaBici", biciService.getTutteLeBici());
        return "Admin/UpdateTariffs";
    }

    @PostMapping("/update-tariffs")
    public String salvaNuovaTariffa(@RequestParam("idBici") Long id,
                                    @RequestParam("prezzo") double prezzo,
                                    RedirectAttributes redirectAttributes) {
        biciService.aggiornaTariffa(id, prezzo);
        redirectAttributes.addFlashAttribute("successMessage", "Tariffa aggiornata correttamente.");
        return "redirect:/admin/update-tariffs";
    }

    // ---------------------------------------------------------
    // 2. GESTIONE EQUIPAGGIAMENTO (LOGICA AGGIORNATA)
    // ---------------------------------------------------------
    @GetMapping("/add-equip")
    public String formEquip(Model model) {
        model.addAttribute("nuovoEquip", new Equipaggiamento());
        return "Admin/CreateEquip";
    }

    @PostMapping("/add-equip")
    public String salvaEquip(@ModelAttribute Equipaggiamento equip, RedirectAttributes redirectAttributes) {
        try {
            // Chiamiamo il metodo "intelligente" che fa la somma se esiste già
            equipService.salvaOAggiorna(equip);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Equipaggiamento '" + equip.getNome() + "' gestito correttamente (Stock aggiornato o Nuovo inserito).");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nel salvataggio: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }
}