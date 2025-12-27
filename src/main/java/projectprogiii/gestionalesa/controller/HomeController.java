package projectprogiii.gestionalesa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projectprogiii.gestionalesa.service.BiciclettaService;

@Controller
public class HomeController {

    @Autowired
    private BiciclettaService biciclettaService;

    // 1. Mostra la pagina di login iniziale
    @GetMapping("/")
    public String Index(){
        return "index.html";
    }

    @GetMapping("/catalogo")
    public String mostraCatalogo(Model model) {
        model.addAttribute("listaBici", biciclettaService.getTutteLeBici());
        return "Catalogo"; // Cerca templates/Catalogo.html (non più in Client/)
    }

}
