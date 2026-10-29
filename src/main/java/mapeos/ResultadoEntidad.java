package mapeos;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "tblResultados",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idPrueba", "idParametro"}))
public class ResultadoEntidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "idPrueba", nullable = false)
    private PruebaEntidad prueba;

    @ManyToOne
    @JoinColumn(name = "idParametro", nullable = false)
    private ParametroEntidad parametro;

    @Column(name = "valor", nullable = false, precision = 12, scale = 4)
    private BigDecimal valor;

    @Column(name = "observacion")
    private String observacion;

    // Copia de lo que valía el parámetro al capturar el resultado
    @Column(name = "rangoInicial", nullable = false, precision = 12, scale = 4)
    private BigDecimal rangoInicial;

    @Column(name = "rangoFinal", nullable = false, precision = 12, scale = 4)
    private BigDecimal rangoFinal;

    @Column(name = "unidadMedida")
    private String unidadMedida;

    public ResultadoEntidad() {
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

    public ParametroEntidad getParametro() {
        return parametro;
    }

    public void setParametro(ParametroEntidad parametro) {
        this.parametro = parametro;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public BigDecimal getRangoInicial() {
        return rangoInicial;
    }

    public void setRangoInicial(BigDecimal rangoInicial) {
        this.rangoInicial = rangoInicial;
    }

    public BigDecimal getRangoFinal() {
        return rangoFinal;
    }

    public void setRangoFinal(BigDecimal rangoFinal) {
        this.rangoFinal = rangoFinal;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    @Override
    public String toString() {
        return "ResultadoEntidad{" + "id=" + id + ", valor=" + valor + ", observacion=" + observacion + '}';
    }
}