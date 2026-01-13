package projectprogiii.gestionalesa.strategy;

// Context
public class PaymentContext {
    private IPaymentStrategy strategy;

    // setter di strategy
    public void setStrategy(IPaymentStrategy strategy) {
        this.strategy = strategy;
    }

    // some_method() nelle slides
    public void executePayment(double amount) {
        if(strategy == null) {
            System.out.println("Nessuna strategia di pagamento selezionata!");
            return;
        }
        strategy.pay(amount);
    }
}