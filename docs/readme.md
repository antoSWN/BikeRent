# 🚲 Progetto BikeRent 

- Esame : Programmazione III 
- Sviluppatori : Polito Antonio & Salzano Antonio

 📌 Requisiti 

Il progetto consiste in un sistema gestionale per il noleggio di biciclette.

# Note progetto : 

###  Gestione dati nel database

Nei requisti c'era la gestione dei dati via files(JSON) o tramite database relazioni,
sebbene i file json sono facili da leggere hanno limti tipo concorrenza, performance e integrità dei dati.
> Per lo sviluppo abbiamo utilizzato H2, un database in-memory relazionale SQL scritto in Java.

--> I dati sono persistenti e salvati localmente nella cartella **/data**

Per far comunicare il codice Java con il database SQL, utilizziamo **Spring Data JPA**.
Questo layer ci permette di mappare le nostre classi (come Bicicletta) direttamente
in tabelle del database tramite l'annotazione **@Entity**

### L'interfaccia Repository

Invece di scrivere manualmente le query SQL, utilizziamo le Repository. Estendendo JpaRepository, Spring genera automaticamente tutto il codice necessario per le operazioni CRUD.

```
public interface BiciclettaRepository extends JpaRepository<Bicicletta, Long> {
    // Tutti i metodi standard (save, delete, find) sono già inclusi!
}
```

La scrittura dei getter/setter nei modelli viene semplificata grazie al uso di una libreria
chiamata "lombok". L'intervento di questa avviene grazie al attributo @Data sopra la dichiarazione
della classe del modello : 

```
@Entity // <-- Spring Data JPA 
@Data // <-- Lombok
@NoArgsConstructor // <--- Lombok che genera anche il costruttore vuoto
public class Bicicletta {

    @Id // Chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (1, 2, 3...)
    private Long id;

    ... Resto codice ...

}
```

### Design Patterns 

I pattern utilizzati sono i seguenti due : 

- **Factory Pattern**

Il Factory Pattern definisce un'interfaccia per creare un oggetto, ma lascia alle sottoclassi la decisione su quale classe istanziare.

Abbiamo utilizzato questo pattern per gestire il sistema di Notifiche. Il sistema è progettato per inviare conferme (ad esempio via Email o SMS) senza che il codice principale debba conoscere i dettagli tecnici dell'invio.

Struttura file : 

- **Interfaccia** : pattern/ServizioNotifica.java
  - definisce il metodo inviaConferma()

- **Prodotti concreti**
  - pattern/concretes/EmailNotifica.java --> implementa l'invio via Email
  - pattern/concretes/SmsNotifica.java   --> implementa l'invio via SMS

- **Factory astratta** 
  - pattern/factories/NotificaFactory.java

- **Factory Concrete**
  - pattern/factories/EmailFactory.java 
  - pattern/factories/SmsFactory.java

Se in futuro volessimo aggiungere altri metodi, basterà creare una nuova classe nella cartella concretes e factories

- **Strategy Pattern**

Lo Strategy Pattern definisce una famiglia di algoritmi, li incapsula separatamente e li rende intercambiabili.

È stato applicato per la gestione dei Pagamenti. Un utente può scegliere di pagare con metodi diversi (Carta di Credito, Contanti, ecc.) e il sistema adatta il calcolo o la procedura di pagamento dinamicamente.

Struttura file : 

- **Interfaccia Strategy**
  - strategy/IPaymentStrategy.java
    - definisce il metodo paga()
- **Strategie concrete**
  - strategy/CreditCardStrategy.java (logica per il pagamento con carta)
  - strategy/CashStrategy.java
- **Context**
  - strategy/PaymentContext.java
    - Questa classe riceve la strategia scelta e la esegue

Perchè tutto questo anzichè un semplice switch case? 

La risposta in breve è --> per rispettare uno dei principi SOLID : **Open/Closed**

Che recità sostanzialemnte che "le entità software dovrebbero essere aperte per l'estensione ma chiuse per le modifiche."

Infatti, se in futuro dobbiamo aggiungere un metodo di pagamento nuovo, es. Bitcoin,
senza questo design pattern dobbiamo aprire il file NoleggioService.java, cercare l'if e modificarlo.

Col design pattern, creiamo una nuova strategy, lo passiamo al context e non tocchiamo codice già esistente.












