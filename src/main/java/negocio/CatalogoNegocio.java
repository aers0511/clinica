package negocio;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import mapeos.EstudioEntidad;
import mapeos.ParametroEntidad;
import mapeos.TipoMuestraEntidad;
import persistencia.Conexion;

// Catálogo de los análisis: agregación de tipos de muestra, estudios y parámetros
// Aquí pues usamos sql clásico xd también en otras clases
public class CatalogoNegocio {

    public TipoMuestraEntidad registrarTipoMuestra(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de muestra es obligatorio");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            TipoMuestraEntidad tipo = new TipoMuestraEntidad(nombre);
            em.persist(tipo);
            em.getTransaction().commit();
            return tipo;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public EstudioEntidad registrarEstudio(String nombre, String nota, int idTipoMuestra) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del estudio es obligatorio");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            TipoMuestraEntidad tipo = em.find(TipoMuestraEntidad.class, idTipoMuestra);
            if (tipo == null) {
                throw new IllegalArgumentException("El tipo de muestra no existe");
            }
            EstudioEntidad estudio = new EstudioEntidad(nombre, nota);
            estudio.setTipoMuestra(tipo);
            em.persist(estudio);
            em.getTransaction().commit();
            return estudio;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public ParametroEntidad registrarParametro(String nombre, int orden, String nota, String unidadMedida,
            BigDecimal rangoInicial, BigDecimal rangoFinal, int edadInicial, int edadFinal, int idEstudio) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del parámetro es obligatorio");
        }
        if (rangoInicial == null || rangoFinal == null || rangoInicial.compareTo(rangoFinal) > 0) {
            throw new IllegalArgumentException("Rango inválido: el inicial no puede ser mayor al final");
        }
        if (edadInicial < 0 || edadInicial > edadFinal) {
            throw new IllegalArgumentException("Rango de edad inválido: la inicial no puede ser mayor a la final");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            EstudioEntidad estudio = em.find(EstudioEntidad.class, idEstudio);
            if (estudio == null) {
                throw new IllegalArgumentException("El estudio no existe");
            }

            Long traslapados = em.createQuery(
                    "SELECT COUNT(p) FROM ParametroEntidad p WHERE p.estudio.id = ?1 AND p.nombre = ?2 "
                    + "AND p.edadInicial <= ?4 AND p.edadFinal >= ?3", Long.class)
                    .setParameter(1, idEstudio)
                    .setParameter(2, nombre)
                    .setParameter(3, edadInicial)
                    .setParameter(4, edadFinal)
                    .getSingleResult();
            if (traslapados > 0) {
                throw new IllegalArgumentException("Ya existe '" + nombre + "' con un rango de edad que se traslapa");
            }
            ParametroEntidad parametro = new ParametroEntidad();
            parametro.setNombre(nombre);
            parametro.setOrden(orden);
            parametro.setNotaDescriptiva(nota);
            parametro.setUnidadMedida(unidadMedida);
            parametro.setRangoInicial(rangoInicial);
            parametro.setRangoFinal(rangoFinal);
            parametro.setEdadInicial(edadInicial);
            parametro.setEdadFinal(edadFinal);
            parametro.setEstudio(estudio);
            em.persist(parametro);
            em.getTransaction().commit();
            return parametro;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<TipoMuestraEntidad> listarTiposMuestra() {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoMuestraEntidad t", TipoMuestraEntidad.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<EstudioEntidad> listarEstudios() {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery("SELECT e FROM EstudioEntidad e", EstudioEntidad.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<ParametroEntidad> listarParametrosPorEstudio(int idEstudio) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM ParametroEntidad p WHERE p.estudio.id = ?1 ORDER BY p.orden", ParametroEntidad.class)
                    .setParameter(1, idEstudio)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
