package projectprogiii.gestionalesa.pattern.factories;

import projectprogiii.gestionalesa.pattern.NotificaFactory;
import projectprogiii.gestionalesa.pattern.ServizioNotifica;
import projectprogiii.gestionalesa.pattern.concretes.EmailNotifica;

public class EmailFactory implements NotificaFactory {
    @Override
    public ServizioNotifica creaNotifica() {
        return new EmailNotifica();
    }
}
