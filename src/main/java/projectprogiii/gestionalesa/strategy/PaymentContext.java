package projectprogiii.gestionalesa.strategy;

public class PaymentContext {
    private IPaymentStrategy strategy;

    public void setStrategy(IPaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void executePayment(double amount) {
        if(strategy == null) {
            System.out.println("Nessuna strategia di pagamento selezionata!");
            return;
        }
        strategy.pay(amount);
    }
}