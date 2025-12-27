package projectprogiii.gestionalesa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projectprogiii.gestionalesa.model.Bicicletta;
import projectprogiii.gestionalesa.model.Parcheggio;

// Estendendo JpaRepository, Spring crea automaticamente le query SQL per noi.
public interface ParcheggioRepository extends JpaRepository<Parcheggio, Long> {
    // Qui è vuoto perché ereditiamo tutto (save, findAll, delete...)
}