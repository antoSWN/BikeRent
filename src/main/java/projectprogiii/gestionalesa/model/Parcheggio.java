package projectprogiii.gestionalesa.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Parcheggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String indirizzo;
    private int capienza;

    public Parcheggio(String nome, String indirizzo, int capienza) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.capienza = capienza;
    }
}