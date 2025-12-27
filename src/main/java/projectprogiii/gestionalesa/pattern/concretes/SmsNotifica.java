package projectprogiii.gestionalesa.pattern.concretes;

import projectprogiii.gestionalesa.pattern.ServizioNotifica;

public class SmsNotifica implements ServizioNotifica {
    @Override
    public void inviaConferma(String destinatario, String messaggio) {
        System.out.println("[SMS] A: " + destinatario + " | Testo: " + messaggio);
    }
}
