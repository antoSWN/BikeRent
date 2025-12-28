package projectprogiii.gestionalesa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Equipaggiamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome; // es. "Casco", "Lucchetto"
    private Integer quantitaDisponibile;

    public  Equipaggiamento(String nome, Integer quantitaDisponibile) {
        this.nome = nome;
        this.quantitaDisponibile = quantitaDisponibile;
    }
}