package mapeos;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tblDoctores")
public class DoctorEntidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombreCompleto", nullable = false)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false)
    private Sexo sexo;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "doctor")
    private List<PruebaEntidad> pruebas;

    public DoctorEntidad() {
    }

    public DoctorEntidad(String nombreCompleto, Sexo sexo) {
        this.nombreCompleto = nombreCompleto;
        this.sexo = sexo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<PruebaEntidad> getPruebas() {
        return pruebas;
    }

    public void setPruebas(List<PruebaEntidad> pruebas) {
        this.pruebas = pruebas;
    }

    @Override
    public String toString() {
        return "DoctorEntidad{" + "id=" + id + ", nombreCompleto=" + nombreCompleto + ", sexo=" + sexo + '}';
    }
}