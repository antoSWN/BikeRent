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

### IDK




