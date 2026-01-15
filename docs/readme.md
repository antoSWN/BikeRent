# Progetto BikeRent - Gestione Noleggio Biciclette

Applicazione Java per la gestione di una flotta di biciclette e relativi equipaggiamenti, sviluppata per il corso di Programmazione III.

## 📄 Relazione Progetto
Potete consultare la documentazione completa qui: [Relazione_Tecnica.pdf](./Relazione_Tecnica.pdf)

## 🛠️ Design Pattern Implementati
Il progetto segue i principi SOLID e utilizza i seguenti pattern della Gang of Four:
* **Factory Method**: Gestione delle notifiche (Email/SMS).
* **Strategy**: Algoritmi di pagamento diversificati (Carta, Contanti, Bancomat).
* **State**: Gestione del ciclo di vita della Bicicletta (Disponibile/Noleggiata).
* **Builder**: Costruzione complessa dell'oggetto Noleggio tramite Director.

## 🚀 Tecnologie utilizzate
* Java 17+ (linguaggio)
* Spring Boot / Spring Data JPA (framework)
* Database H2
* Maven (packages)