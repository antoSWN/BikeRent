package projectprogiii.gestionalesa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projectprogiii.gestionalesa.model.Noleggio;

import java.util.List;
import java.util.Optional;

public interface NoleggioRepository extends JpaRepository<Noleggio, Long> {

    // Trova un noleggio per questo utente che non è ancora finito
    List<Noleggio> findByUsernameAndDataFineIsNull(String username);}