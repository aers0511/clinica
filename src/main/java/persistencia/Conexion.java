package persistencia;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Conexion {

    private static final EntityManagerFactory emf
            = Persistence.createEntityManagerFactory("ConexionJPA");

    public static EntityManager crearEntityManager() {
        return emf.createEntityManager();
    }

    public static void cerrar() {
        emf.close();
    }
}