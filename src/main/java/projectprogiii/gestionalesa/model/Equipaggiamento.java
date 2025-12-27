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
    private double prezzo;

    public  Equipaggiamento(String nome, double prezzo) {
        this.nome = nome;
        this.prezzo = prezzo;
    }
}