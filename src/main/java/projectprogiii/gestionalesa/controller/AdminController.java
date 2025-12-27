package projectprogiii.gestionalesa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Attenzione agli import!
import org.springframework.web.bind.annotation.*;
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Equipaggiamento;
import projectprogiii.gestionalesa.service.BiciclettaService;
import projectprogiii.gestionalesa.service.EquipaggiamentoService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BiciclettaService biciService;

    @Autowired
    private EquipaggiamentoService equipService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("listaBici", biciService.getTutteLeBici());
        return "Admin/Admin_dashboard";
    }

    // 1. MOSTRA IL FORM PER AGGIUNGERE BICI
    @GetMapping("/add-bike")
    public String showAddBikeForm(Model model) {

        // Oggetto vuoto
        model.addAttribute("nuovaBici", new Bicicletta());
        return "Admin/CreateBike";
    }

    // 2. RICEVE I DATI DAL FORM E SALVA
    @PostMapping("/add-bike")
    public String saveBike(@ModelAttribute Bicicletta bici) {
        // Salvataggio della bici nel db locale
        biciService.salvaBicicletta(bici);

        // Dopo aver salvato, torniamo alla dashboard (o lista bici)
        return "redirect:/admin/dashboard";
    }

    // --- 2. AGGIUNGERE EQUIPAGGIAMENTO ---
    @GetMapping("/add-equip")
    public String formEquip(Model model) {
        model.addAttribute("nuovoEquip", new Equipaggiamento());
        return "Admin/CreateEquip";
    }

    @PostMapping("/add-equip")
    public String salvaEquip(@ModelAttribute Equipaggiamento equip) {
        equipService.salvaEquipaggiamento(equip);
        return "redirect:/admin/dashboard";
    }

    // --- 3. AGGIORNARE TARIFFE ---
    // Mostriamo una tabella con tutte le bici ed i prezzi attuali
    @GetMapping("/update-tariffs")
    public String mostraTariffe(Model model) {
        model.addAttribute("listaBici", biciService.getTutteLeBici());
        return "Admin/UpdateTariffs";
    }

    // Riceviamo ID e Nuovo Prezzo dal form
    @PostMapping("/update-tariffs")
    public String salvaNuovaTariffa(@RequestParam("idBici") Long id,
                                    @RequestParam("prezzo") double prezzo) {
        biciService.aggiornaTariffa(id, prezzo);
        return "redirect:/admin/update-tariffs"; // Ricarica la pagina stessa
    }

}