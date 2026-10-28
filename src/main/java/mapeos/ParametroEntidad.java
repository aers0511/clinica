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
@Table(name = "tblParametros")
public class ParametroEntidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "orden", nullable = false)
    private int orden;

    @Column(name = "notaDescriptiva")
    private String notaDescriptiva;

    @Column(name = "unidadMedida")
    private String unidadMedida;

    @Column(name = "rangoInicial")
    private double rangoInicial;

    @Column(name = "rangoFinal")
    private double rangoFinal;

    @Column(name = "edadInicial")
    private int edadInicial;

    @Column(name = "edadFinal")
    private int edadFinal;

    @ManyToOne
    @JoinColumn(name = "idEstudio", nullable = false)
    private EstudioEntidad estudio;

    @OneToMany(mappedBy = "parametro")
    private List<ResultadoEntidad> resultados;

    public ParametroEntidad() {
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

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getNotaDescriptiva() {
        return notaDescriptiva;
    }

    public void setNotaDescriptiva(String notaDescriptiva) {
        this.notaDescriptiva = notaDescriptiva;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getRangoInicial() {
        return rangoInicial;
    }

    public void setRangoInicial(double rangoInicial) {
        this.rangoInicial = rangoInicial;
    }

    public double getRangoFinal() {
        return rangoFinal;
    }

    public void setRangoFinal(double rangoFinal) {
        this.rangoFinal = rangoFinal;
    }

    public int getEdadInicial() {
        return edadInicial;
    }

    public void setEdadInicial(int edadInicial) {
        this.edadInicial = edadInicial;
    }

    public int getEdadFinal() {
        return edadFinal;
    }

    public void setEdadFinal(int edadFinal) {
        this.edadFinal = edadFinal;
    }

    public EstudioEntidad getEstudio() {
        return estudio;
    }

    public void setEstudio(EstudioEntidad estudio) {
        this.estudio = estudio;
    }

    public List<ResultadoEntidad> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoEntidad> resultados) {
        this.resultados = resultados;
    }

    @Override
    public String toString() {
        return "ParametroEntidad{" + "id=" + id + ", nombre=" + nombre + ", orden=" + orden + ", unidadMedida=" + unidadMedida
                + ", rangoInicial=" + rangoInicial + ", rangoFinal=" + rangoFinal
                + ", edadInicial=" + edadInicial + ", edadFinal=" + edadFinal + '}';
    }
}