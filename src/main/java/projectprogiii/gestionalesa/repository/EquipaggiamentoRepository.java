package projectprogiii.gestionalesa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projectprogiii.gestionalesa.model.Equipaggiamento;

@Repository
public interface EquipaggiamentoRepository extends JpaRepository<Equipaggiamento, Long> {
    // Non serve scrivere nulla qui per ora.
    // JpaRepository ci regala già: save(), findAll(), findById(), delete(), ecc.
}