package projectprogiii.gestionalesa.pattern;

// Factory / Creator --> classe astratta
public abstract class  NotificaFactory {
    // metodo "operativo" (anOperation delle slide), questo è il metodo che il Service chiamerà
    public void mandaNotifica(String destinatario, String messaggio) {
        // 1. Chiedo alla sottoclasse l'oggetto giusto
        ServizioNotifica notifica = creaNotifica(); // --> ritorna un oggetto

        // 2. Uso l'oggetto ritornato
        notifica.inviaConferma(destinatario, messaggio);
    }

    // Protected perché deve essere visto solo dalle sottoclassi e da questa classe
    protected abstract ServizioNotifica creaNotifica();
}
