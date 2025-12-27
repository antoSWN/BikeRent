package projectprogiii.gestionalesa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projectprogiii.gestionalesa.model.Bicicletta;

// Estendendo JpaRepository, Spring crea automaticamente le query SQL per noi.
public interface BiciclettaRepository extends JpaRepository<Bicicletta, Long> {
    // Qui è vuoto perché ereditiamo tutto (save, findAll, delete...)
}