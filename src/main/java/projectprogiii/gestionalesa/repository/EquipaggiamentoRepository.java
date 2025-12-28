package projectprogiii.gestionalesa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectprogiii.gestionalesa.model.Equipaggiamento;

import java.util.Optional;

@Repository
public interface EquipaggiamentoRepository extends JpaRepository<Equipaggiamento, Long> {
    // Non serve scrivere nulla qui per ora.
    Optional<Equipaggiamento> findByNomeIgnoreCase(String nome);
    // JpaRepository ci regala già: save(), findAll(), findById(), delete(), ecc.
}