package persistencia;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Conexion {

    // Para no andar haciendo una en cada entidad hizimos la fabrica, así coo nos dijo en el parcial 1
    private static final EntityManagerFactory emf
            = Persistence.createEntityManagerFactory("ConexionJPA");

    public static EntityManager crearEntityManager() {
        return emf.createEntityManager();
    }

    // Deshace la transacción si algo falló a la mitad del proceso
    public static void cancelar(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    public static void cerrar() {
        emf.close();
    }
}