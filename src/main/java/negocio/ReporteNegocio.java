package negocio;

import java.time.Period;
import java.util.List;
import javax.persistence.EntityManager;
import mapeos.EmpresaEntidad;
import mapeos.ParametroEntidad;
import mapeos.PruebaEntidad;
import mapeos.ResultadoEntidad;
import persistencia.Conexion;

// Emisión de reportes: hace el texto del reporte de una prueba
// Aún no lo preparamos para el pdf
public class ReporteNegocio {

    private final ResultadoNegocio resultadoNegocio = new ResultadoNegocio();

    public String generarReporte(int idPrueba) {
        PruebaEntidad prueba;
        List<EmpresaEntidad> empresas;
        EntityManager em = Conexion.crearEntityManager();
        try {
            prueba = em.find(PruebaEntidad.class, idPrueba);
            if (prueba == null) {
                throw new IllegalArgumentException("La prueba no existe");
            }
            empresas = em.createQuery("SELECT e FROM EmpresaEntidad e ORDER BY e.id", EmpresaEntidad.class)
                    .setMaxResults(1)
                    .getResultList();
        } finally {
            em.close();
        }

        List<ResultadoEntidad> resultados = resultadoNegocio.listarResultadosPorPrueba(idPrueba);
        if (resultados.isEmpty()) {
            throw new IllegalStateException("La prueba aún no tiene resultados capturados");
        }

        // El reporte solo se emite cuando todos los parámetros de los estudios solicitados tienen resultado
        List<ParametroEntidad> faltantes = resultadoNegocio.parametrosFaltantes(idPrueba);
        if (!faltantes.isEmpty()) {
            StringBuilder nombres = new StringBuilder();
            for (ParametroEntidad p : faltantes) {
                if (nombres.length() > 0) {
                    nombres.append(", ");
                }
                nombres.append(p.getNombre());
            }
            throw new IllegalStateException("Faltan resultados por capturar: " + nombres);
        }

        StringBuilder sb = new StringBuilder();

        if (!empresas.isEmpty()) {
            EmpresaEntidad empresa = empresas.get(0);
            sb.append(empresa.getNombre()).append("\n");
            sb.append("RFC: ").append(empresa.getRfc()).append("\n");
            sb.append(empresa.getDireccion()).append(" Tel: ").append(empresa.getTelefono()).append("\n");
            sb.append("Cédula: ").append(empresa.getCedula()).append("\n\n");
        }

        int edad = Period.between(prueba.getCliente().getFechaNacimiento(), prueba.getFechaHora().toLocalDate()).getYears();
        String doctor = prueba.getDoctor() == null ? "A quien corresponda" : prueba.getDoctor().getNombreCompleto();
        sb.append("Folio: ").append(prueba.getFolio()).append("\n");
        sb.append("Fecha: ").append(prueba.getFechaHora()).append("\n");
        sb.append("Cliente: ").append(prueba.getCliente().getNombreCompleto())
                .append(" | Edad: ").append(edad)
                .append(" | Sexo: ").append(prueba.getCliente().getSexo())
                .append(" | Sangre: ").append(prueba.getCliente().getTipoSangre()).append("\n");
        sb.append("Doctor: ").append(doctor).append("\n");

        String estudioActual = "";
        for (ResultadoEntidad r : resultados) {
            String estudio = r.getParametro().getEstudio().getNombre();
            if (!estudio.equals(estudioActual)) {
                estudioActual = estudio;
                sb.append("\n== ").append(estudio)
                        .append(" (").append(r.getParametro().getEstudio().getTipoMuestra().getNombre()).append(") ==\n");
            }

            sb.append(r.getParametro().getNombre()).append(": ")
                    .append(r.getValor().toPlainString()).append(" ").append(r.getUnidadMedida())
                    .append(" [").append(r.getRangoInicial().toPlainString())
                    .append(" - ").append(r.getRangoFinal().toPlainString()).append("]");
            if (r.getObservacion() != null && !r.getObservacion().isBlank()) {
                sb.append(" Observación: ").append(r.getObservacion());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
