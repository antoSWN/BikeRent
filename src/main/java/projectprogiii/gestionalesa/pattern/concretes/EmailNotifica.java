package projectprogiii.gestionalesa.pattern.concretes;

import projectprogiii.gestionalesa.pattern.ServizioNotifica;

// Concrete Product
public class EmailNotifica implements ServizioNotifica {
    @Override
    public void inviaConferma(String destinatario, String messaggio) {
        System.out.println("[EMAIL] A: " + destinatario + " | Testo: " + messaggio);
    }
}
