package mapeos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
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
@Table(name = "tblPruebas")
public class PruebaEntidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "folio", nullable = false, unique = true)
    private String folio;

    @Column(name = "fechaHora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false)
    private ClienteEntidad cliente;

    // Null cuando el cliente va por cuenta propia ("a quien corresponda")
    @ManyToOne
    @JoinColumn(name = "idDoctor")
    private DoctorEntidad doctor;

    @OneToMany(mappedBy = "prueba", cascade = CascadeType.PERSIST)
    private List<PruebaEstudioEntidad> estudios;

    @OneToMany(mappedBy = "prueba")
    private List<ResultadoEntidad> resultados;

    public PruebaEntidad() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public ClienteEntidad getCliente() {
        return cliente;
    }

    public void setCliente(ClienteEntidad cliente) {
        this.cliente = cliente;
    }

    public DoctorEntidad getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorEntidad doctor) {
        this.doctor = doctor;
    }

    public List<PruebaEstudioEntidad> getEstudios() {
        return estudios;
    }

    public void setEstudios(List<PruebaEstudioEntidad> estudios) {
        this.estudios = estudios;
    }

    public List<ResultadoEntidad> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoEntidad> resultados) {
        this.resultados = resultados;
    }

    @Override
    public String toString() {
        return "PruebaEntidad{" + "id=" + id + ", folio=" + folio + ", fechaHora=" + fechaHora + '}';
    }
}