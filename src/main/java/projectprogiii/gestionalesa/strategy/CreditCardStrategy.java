package projectprogiii.gestionalesa.strategy;

public class CreditCardStrategy implements IPaymentStrategy {
    private String cardNumber;

    public CreditCardStrategy(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Pagato " + amount + " con Carta: " + cardNumber);
    }
}
