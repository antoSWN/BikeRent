package projectprogiii.gestionalesa.pattern.factories;

import projectprogiii.gestionalesa.pattern.NotificaFactory;
import projectprogiii.gestionalesa.pattern.ServizioNotifica;
import projectprogiii.gestionalesa.pattern.concretes.EmailNotifica;

// Concrete Factory
public class EmailFactory extends NotificaFactory {
    @Override
    protected ServizioNotifica creaNotifica() {
        return new EmailNotifica();
    }
}
