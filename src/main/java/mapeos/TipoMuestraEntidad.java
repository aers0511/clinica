package mapeos;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tblTiposMuestra")
public class TipoMuestraEntidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "tipoMuestra")
    private List<EstudioEntidad> estudios;

    public TipoMuestraEntidad() {
    }

    public TipoMuestraEntidad(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<EstudioEntidad> getEstudios() {
        return estudios;
    }

    public void setEstudios(List<EstudioEntidad> estudios) {
        this.estudios = estudios;
    }

    @Override
    public String toString() {
        return "TipoMuestraEntidad{" + "id=" + id + ", nombre=" + nombre + '}';
    }
}