package projectprogiii.gestionalesa.controller;

import jakarta.servlet.http.HttpSession; // O javax.servlet se usi Spring Boot vecchio
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // 1. MOSTRA LA PAGINA DI LOGIN
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 2. GESTISCE IL LOGOUT
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Questa riga distrugge la sessione (cancella i dati dell'utente loggato)
        session.invalidate();

        // Reindirizza al login aggiungendo ?logout per mostrare il messaggio verde
        return "redirect:/login?logout";
    }

    // 3. GESTIONE LOGIN MANUALE
    @PostMapping("/login")
    public String elaboraLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session
    ) {

        if ("admin".equals(username) && "admin".equals(password)) {
            session.setAttribute("utenteLoggato", "admin");
            session.setAttribute("nomeVisualizzato", "John Admin"); // <--- NOME DECISO QUA
            session.setAttribute("ruolo", "ADMIN"); // Utile per i link
            return "redirect:/admin/dashboard";
        }
        else if ("client".equals(username) && "client".equals(password)) {
            session.setAttribute("utenteLoggato", "client");
            session.setAttribute("nomeVisualizzato", "Mario Rossi"); // <--- NOME DECISO QUA
            session.setAttribute("ruolo", "CLIENT");
            return "redirect:/client/home";
        }
        else {
            // Login fallito: torna al login con parametro error
            return "redirect:/login?error";
        }
    }
}