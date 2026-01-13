package projectprogiii.gestionalesa.strategy;

// ConcreteStrategyX
public class CashStrategy implements IPaymentStrategy {

    @Override
    public void pay(double amount) {
        System.out.println("Pagato " + amount + " in Contanti.");
    }
}