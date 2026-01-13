package projectprogiii.gestionalesa.pattern.factories;

import projectprogiii.gestionalesa.pattern.NotificaFactory;
import projectprogiii.gestionalesa.pattern.ServizioNotifica;
import projectprogiii.gestionalesa.pattern.concretes.SmsNotifica;

// Concrete Factory
public class SmsFactory extends NotificaFactory {
    @Override
    protected ServizioNotifica creaNotifica() {
        return new SmsNotifica();
    }
}
