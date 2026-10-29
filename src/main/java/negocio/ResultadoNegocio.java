
package negocio;

import java.math.BigDecimal;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import mapeos.EstudioEntidad;
import mapeos.ParametroEntidad;
import mapeos.PruebaEntidad;
import mapeos.ResultadoEntidad;
import persistencia.Conexion;


public class ResultadoNegocio {

    public List<ParametroEntidad> parametrosAplicables(int idPrueba, int idEstudio) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            PruebaEntidad prueba = buscarPrueba(em, idPrueba);
            return aplicables(em, calcularEdad(prueba), idEstudio);
        } finally {
            em.close();
        }
    }

    public ResultadoEntidad registrarResultado(int idPrueba, int idParametro, BigDecimal valor, String observacion) {
        if (valor == null) {
            throw new IllegalArgumentException("El valor es obligatorio");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();

            PruebaEntidad prueba = buscarPrueba(em, idPrueba);
            ParametroEntidad parametro = em.find(ParametroEntidad.class, idParametro);
            if (parametro == null) {
                throw new IllegalArgumentException("El parámetro no existe");
            }

            // El parámetro debe pertenecer a un estudio solicitado en la prueba
            Long solicitado = em.createQuery(
                    "SELECT COUNT(pe) FROM PruebaEstudioEntidad pe WHERE pe.prueba.id = :idPrueba AND pe.estudio.id = :idEstudio", Long.class)
                    .setParameter("idPrueba", idPrueba)
                    .setParameter("idEstudio", parametro.getEstudio().getId())
                    .getSingleResult();
            if (solicitado == 0) {
                throw new IllegalArgumentException("El parámetro pertenece a un estudio que no se solicitó en esta prueba");
            }

            // La edad debe ser dentro del rango de la prueba
            int edad = calcularEdad(prueba);
            if (edad < parametro.getEdadInicial() || edad > parametro.getEdadFinal()) {
                throw new IllegalArgumentException("El parámetro no aplica a la edad del cliente (" + edad + " años)");
            }

            // Un solo resultado por parámetro en cada prueba
            Long repetido = em.createQuery(
                    "SELECT COUNT(r) FROM ResultadoEntidad r WHERE r.prueba.id = :idPrueba AND r.parametro.id = :idParametro", Long.class)
                    .setParameter("idPrueba", idPrueba)
                    .setParameter("idParametro", idParametro)
                    .getSingleResult();
            if (repetido > 0) {
                throw new IllegalArgumentException("Ese parámetro ya tiene resultado en esta prueba");
            }

            ResultadoEntidad resultado = new ResultadoEntidad();
            resultado.setPrueba(prueba);
            resultado.setParametro(parametro);
            resultado.setValor(valor);
            resultado.setObservacion(observacion);
            
            // Aquí e copian los rangos y la unidad vigentes para que el reporte no cambie si el catálogo se edita
            resultado.setRangoInicial(parametro.getRangoInicial());
            resultado.setRangoFinal(parametro.getRangoFinal());
            resultado.setUnidadMedida(parametro.getUnidadMedida());
            em.persist(resultado);

            em.getTransaction().commit();
            return resultado;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<ResultadoEntidad> listarResultadosPorPrueba(int idPrueba) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery(
                    "SELECT r FROM ResultadoEntidad r WHERE r.prueba.id = :idPrueba "
                    + "ORDER BY r.parametro.estudio.id, r.parametro.orden", ResultadoEntidad.class)
                    .setParameter("idPrueba", idPrueba)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Parámetros aplicables de los estudios solicitados que todavía no tienen resultado
    public List<ParametroEntidad> parametrosFaltantes(int idPrueba) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            PruebaEntidad prueba = buscarPrueba(em, idPrueba);
            int edad = calcularEdad(prueba);

            List<Integer> capturados = em.createQuery(
                    "SELECT r.parametro.id FROM ResultadoEntidad r WHERE r.prueba.id = :idPrueba", Integer.class)
                    .setParameter("idPrueba", idPrueba)
                    .getResultList();
            List<EstudioEntidad> estudios = em.createQuery(
                    "SELECT pe.estudio FROM PruebaEstudioEntidad pe WHERE pe.prueba.id = :idPrueba ORDER BY pe.estudio.id",
                    EstudioEntidad.class)
                    .setParameter("idPrueba", idPrueba)
                    .getResultList();

            List<ParametroEntidad> faltantes = new ArrayList<>();
            for (EstudioEntidad estudio : estudios) {
                for (ParametroEntidad p : aplicables(em, edad, estudio.getId())) {
                    if (!capturados.contains(p.getId())) {
                        faltantes.add(p);
                    }
                }
            }
            return faltantes;
        } finally {
            em.close();
        }
    }

    private List<ParametroEntidad> aplicables(EntityManager em, int edad, int idEstudio) {
        return em.createQuery(
                "SELECT p FROM ParametroEntidad p WHERE p.estudio.id = :idEstudio "
                + "AND p.edadInicial <= :edad AND p.edadFinal >= :edad ORDER BY p.orden", ParametroEntidad.class)
                .setParameter("idEstudio", idEstudio)
                .setParameter("edad", edad)
                .getResultList();
    }

    private PruebaEntidad buscarPrueba(EntityManager em, int idPrueba) {
        PruebaEntidad prueba = em.find(PruebaEntidad.class, idPrueba);
        if (prueba == null) {
            throw new IllegalArgumentException("La prueba no existe");
        }
        return prueba;
    }

    
    private int calcularEdad(PruebaEntidad prueba) {
        return Period.between(prueba.getCliente().getFechaNacimiento(), prueba.getFechaHora().toLocalDate()).getYears();
    }
}
