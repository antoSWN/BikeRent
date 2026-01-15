package projectprogiii.gestionalesa.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import projectprogiii.gestionalesa.model.*;
import projectprogiii.gestionalesa.repository.BiciclettaRepository;
import projectprogiii.gestionalesa.repository.EquipaggiamentoRepository;
import projectprogiii.gestionalesa.repository.ParcheggioRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
            BiciclettaRepository biciRepo,
            EquipaggiamentoRepository equipRepo,
            ParcheggioRepository parkRepo
    ) {
        return args -> {

            // --- STEP 1: CREA E SALVA I PARCHEGGI ---
            // È fondamentale salvarli PRIMA delle bici.
            // Quando fai .save(), Spring restituisce l'oggetto con l'ID assegnato dal DB.

            Parcheggio pBrin = new Parcheggio("Parcheggio Brin", "Via Alessandro Volta", 800);
            Parcheggio pSuper = new Parcheggio("Supergarage", "Via Shelter 11", 300);
            Parcheggio pMorelli = new Parcheggio("Garage Morelli", "Viale Morelli", 250);

            // Controllo se il DB è vuoto per evitare duplicati
            if (parkRepo.count() == 0) {
                System.out.println("Salvataggio Parcheggi e generazione ID...");

                // NOTA BENE: Sovrascrivo la variabile con il risultato del save
                pBrin = parkRepo.save(pBrin);       // Ora pBrin ha un ID (es. 1)
                pSuper = parkRepo.save(pSuper);     // Ora pSuper ha un ID (es. 2)
                pMorelli = parkRepo.save(pMorelli); // Ora pMorelli ha un ID (es. 3)
            } else {
                // Se esistono già, li recuperiamo dal DB altrimenti pBrin sarebbe un oggetto nuovo senza ID
                List<Parcheggio> esistenti = parkRepo.findAll();
                if (!esistenti.isEmpty()) {
                    pBrin = esistenti.get(0);
                    pSuper = (esistenti.size() > 1) ? esistenti.get(1) : esistenti.get(0);
                    pMorelli = (esistenti.size() > 2) ? esistenti.get(2) : esistenti.get(0);
                }
            }

            // --- STEP 2: CREA LE BICI E ASSEGNA L'OGGETTO PARCHEGGIO ---
            if (biciRepo.count() == 0) {
                System.out.println("Salvataggio Biciclette collegate ai Parcheggi...");

                List<Bicicletta> biciDaSalvare = new ArrayList<>();

                // 1. Categoria: PASSEGGIO
                Bicicletta b1 = new Bicicletta("Bianchi Spillo", "Passeggio", 4.0, 12);
                b1.setParcheggio(pBrin);
                biciDaSalvare.add(b1);

                Bicicletta b2 = new Bicicletta("Atala Città", "Passeggio", 3.5, 5);
                b2.setParcheggio(pBrin);
                biciDaSalvare.add(b2);

                // 2. Categoria: CORSA
                Bicicletta b3 = new Bicicletta("Pinarello Dogma", "Corsa", 15.0, 3);
                b3.setParcheggio(pMorelli);
                biciDaSalvare.add(b3);

                // 3. Categoria: E-BIKE
                Bicicletta b4 = new Bicicletta("Nilox X5", "E-Bike", 8.0, 7);
                b4.setParcheggio(pSuper);
                biciDaSalvare.add(b4);

                // 4. Categoria: MOUNTAIN BIKE
                Bicicletta b5 = new Bicicletta("Rockrider ST", "Mountain Bike", 6.0, 2);
                b5.setParcheggio(pSuper);
                biciDaSalvare.add(b5);

                // Salvataggio finale delle bici
                biciRepo.saveAll(biciDaSalvare);
            }

            // --- STEP 3: EQUIPAGGIAMENTI ---
            //prova push da dispositivo personale
            if (equipRepo.count() == 0) {
                System.out.println("Salvataggio Equipaggiamenti di base...");
                equipRepo.saveAll(Arrays.asList(
                        new Equipaggiamento("Casco Protettivo", 5),
                        new Equipaggiamento("Seggiolino Bimbo", 5)
                ));
            }
        };
    }
}