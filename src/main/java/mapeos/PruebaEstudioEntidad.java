package mapeos;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

// Estudios que se solicitaron en una prueba, con la revisión del profe llegamos a esto
@Entity
@Table(name = "tblPruebaEstudios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idPrueba", "idEstudio"}))
public class PruebaEstudioEntidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "idPrueba", nullable = false)
    private PruebaEntidad prueba;

    @ManyToOne
    @JoinColumn(name = "idEstudio", nullable = false)
    private EstudioEntidad estudio;

    public PruebaEstudioEntidad() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PruebaEntidad getPrueba() {
        return prueba;
    }

    public void setPrueba(PruebaEntidad prueba) {
        this.prueba = prueba;
    }

    public EstudioEntidad getEstudio() {
        return estudio;
    }

    public void setEstudio(EstudioEntidad estudio) {
        this.estudio = estudio;
    }
}