package mapeos;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tblEstudios")
public class EstudioEntidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "notaDescriptiva")
    private String notaDescriptiva;

    @ManyToOne
    @JoinColumn(name = "idTipoMuestra", nullable = false)
    private TipoMuestraEntidad tipoMuestra;

    @OneToMany(mappedBy = "estudio")
    private List<ParametroEntidad> parametros;

    public EstudioEntidad() {
    }

    public EstudioEntidad(String nombre, String notaDescriptiva) {
        this.nombre = nombre;
        this.notaDescriptiva = notaDescriptiva;
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

    public String getNotaDescriptiva() {
        return notaDescriptiva;
    }

    public void setNotaDescriptiva(String notaDescriptiva) {
        this.notaDescriptiva = notaDescriptiva;
    }

    public TipoMuestraEntidad getTipoMuestra() {
        return tipoMuestra;
    }

    public void setTipoMuestra(TipoMuestraEntidad tipoMuestra) {
        this.tipoMuestra = tipoMuestra;
    }

    public List<ParametroEntidad> getParametros() {
        return parametros;
    }

    public void setParametros(List<ParametroEntidad> parametros) {
        this.parametros = parametros;
    }

    @Override
    public String toString() {
        return "EstudioEntidad{" + "id=" + id + ", nombre=" + nombre + ", notaDescriptiva=" + notaDescriptiva + '}';
    }
}