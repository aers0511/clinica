
package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import mapeos.ClienteEntidad;
import mapeos.DoctorEntidad;
import mapeos.EstudioEntidad;
import mapeos.PruebaEntidad;
import mapeos.PruebaEstudioEntidad;
import mapeos.Sexo;
import mapeos.TipoSangre;
import persistencia.Conexion;

public class SolicitudNegocio {

    public ClienteEntidad registrarCliente(String nombreCompleto, LocalDate fechaNacimiento, Sexo sexo, TipoSangre tipoSangre) {
        if (nombreCompleto == null || nombreCompleto.isBlank() || fechaNacimiento == null
                || sexo == null || tipoSangre == null) {
            throw new IllegalArgumentException("Nombre, fecha de nacimiento, sexo y tipo de sangre son obligatorios");
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no está dentro del rango valido");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            ClienteEntidad cliente = new ClienteEntidad(nombreCompleto, fechaNacimiento, sexo, tipoSangre);
            em.persist(cliente);
            em.getTransaction().commit();
            return cliente;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public DoctorEntidad registrarDoctor(String nombreCompleto, Sexo sexo) {
        if (nombreCompleto == null || nombreCompleto.isBlank() || sexo == null) {
            throw new IllegalArgumentException("Nombre y sexo son obligatorios");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            DoctorEntidad doctor = new DoctorEntidad(nombreCompleto, sexo);
            em.persist(doctor);
            em.getTransaction().commit();
            return doctor;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    // idDoctor = null cuando el cliente va por cuenta propia o sea, "a quien corresponda"
    public PruebaEntidad registrarPrueba(int idCliente, Integer idDoctor, List<Integer> idsEstudios) {
        if (idsEstudios == null || idsEstudios.isEmpty()) {
            throw new IllegalArgumentException("Debe solicitar al menos un estudio");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();

            ClienteEntidad cliente = em.find(ClienteEntidad.class, idCliente);
            if (cliente == null || !cliente.isActivo()) {
                throw new IllegalArgumentException("El cliente no existe o está dado de baja");
            }
            DoctorEntidad doctor = null;
            if (idDoctor != null) {
                doctor = em.find(DoctorEntidad.class, idDoctor);
                if (doctor == null || !doctor.isActivo()) {
                    throw new IllegalArgumentException("El doctor no existe o está dado de baja");
                }
            }

            LocalDateTime ahora = LocalDateTime.now();
            int edad = Period.between(cliente.getFechaNacimiento(), ahora.toLocalDate()).getYears();

            PruebaEntidad prueba = new PruebaEntidad();
            prueba.setFolio(UUID.randomUUID().toString()); // temporal, se reemplaza al obtener el id
            prueba.setFechaHora(ahora);
            prueba.setCliente(cliente);
            prueba.setDoctor(doctor);

            List<PruebaEstudioEntidad> relaciones = new ArrayList<>();
            for (int idEstudio : new LinkedHashSet<>(idsEstudios)) {
                EstudioEntidad estudio = em.find(EstudioEntidad.class, idEstudio);
                if (estudio == null) {
                    throw new IllegalArgumentException("El estudio " + idEstudio + " no existe");
                }
                // El estudio debe tener al menos un parámetro que aplique a la edad del cliente si no pues no
                Long aplicables = em.createQuery(
                        "SELECT COUNT(p) FROM ParametroEntidad p WHERE p.estudio.id = :idEstudio "
                        + "AND p.edadInicial <= :edad AND p.edadFinal >= :edad", Long.class)
                        .setParameter("idEstudio", idEstudio)
                        .setParameter("edad", edad)
                        .getSingleResult();
                if (aplicables == 0) {
                    throw new IllegalArgumentException("El estudio '" + estudio.getNombre()
                            + "' no tiene parámetros para la edad del cliente (" + edad + " años)");
                }
                PruebaEstudioEntidad relacion = new PruebaEstudioEntidad();
                relacion.setPrueba(prueba);
                relacion.setEstudio(estudio);
                relaciones.add(relacion);
            }
            prueba.setEstudios(relaciones);

            // el método cascade = PERSIST se guardan la prueba y sus estudios juntos
            em.persist(prueba);
            em.flush(); // genera el id
            prueba.setFolio(String.format("LAB-%06d", prueba.getId()));

            em.getTransaction().commit();
            return prueba;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public void desactivarCliente(int idCliente) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            ClienteEntidad cliente = em.find(ClienteEntidad.class, idCliente);
            if (cliente == null) {
                throw new IllegalArgumentException("El cliente no existe");
            }
            cliente.setActivo(false);
            em.merge(cliente);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public void desactivarDoctor(int idDoctor) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            DoctorEntidad doctor = em.find(DoctorEntidad.class, idDoctor);
            if (doctor == null) {
                throw new IllegalArgumentException("El doctor no existe");
            }
            doctor.setActivo(false);
            em.merge(doctor);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    public List<ClienteEntidad> listarClientes() {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery("SELECT c FROM ClienteEntidad c WHERE c.activo = true", ClienteEntidad.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<DoctorEntidad> listarDoctores() {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery("SELECT d FROM DoctorEntidad d WHERE d.activo = true", DoctorEntidad.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<PruebaEntidad> listarPruebasPorCliente(int idCliente) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM PruebaEntidad p WHERE p.cliente.id = :idCliente ORDER BY p.fechaHora DESC", PruebaEntidad.class)
                    .setParameter("idCliente", idCliente)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<PruebaEntidad> listarPruebasPorDoctor(int idDoctor) {
        EntityManager em = Conexion.crearEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM PruebaEntidad p WHERE p.doctor.id = :idDoctor ORDER BY p.fechaHora DESC", PruebaEntidad.class)
                    .setParameter("idDoctor", idDoctor)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
