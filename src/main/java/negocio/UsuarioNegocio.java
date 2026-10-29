
package negocio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import mapeos.UsuarioEntidad;
import persistencia.Conexion;

// La contraseña se guarda como "sal:hash" (PBKDF2)
public class UsuarioNegocio {

    private static final int ITERACIONES = 120000;
    private static final int LONGITUD_BITS = 256;

    public UsuarioEntidad registrarUsuario(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.length() < 8) {
            throw new IllegalArgumentException("Usuario obligatorio y contraseña de al menos 8 caracteres");
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            em.getTransaction().begin();
            Long existentes = em.createQuery(
                    "SELECT COUNT(u) FROM UsuarioEntidad u WHERE u.usuario = :usuario", Long.class)
                    .setParameter("usuario", usuario)
                    .getSingleResult();
            if (existentes > 0) {
                throw new IllegalArgumentException("El usuario ya existe");
            }
            UsuarioEntidad nuevo = new UsuarioEntidad();
            nuevo.setUsuario(usuario);
            nuevo.setContrasena(hashear(contrasena));
            em.persist(nuevo);
            em.getTransaction().commit();
            return nuevo;
        } catch (RuntimeException e) {
            Conexion.cancelar(em);
            throw e;
        } finally {
            em.close();
        }
    }

    // Devuelve el usuario si las credenciales son correctas; si no, null
    public UsuarioEntidad iniciarSesion(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return null;
        }
        EntityManager em = Conexion.crearEntityManager();
        try {
            List<UsuarioEntidad> encontrados = em.createQuery(
                    "SELECT u FROM UsuarioEntidad u WHERE u.usuario = :usuario", UsuarioEntidad.class)
                    .setParameter("usuario", usuario)
                    .getResultList();
            if (encontrados.isEmpty()) {
                return null;
            }
            UsuarioEntidad encontrado = encontrados.get(0);
            return verificar(contrasena, encontrado.getContrasena()) ? encontrado : null;
        } finally {
            em.close();
        }
    }

    private String hashear(String contrasena) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(pbkdf2(contrasena, sal));
    }

    private boolean verificar(String contrasena, String guardado) {
        String[] partes = guardado.split(":");
        byte[] sal = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        return MessageDigest.isEqual(esperado, pbkdf2(contrasena, sal));
    }

    private byte[] pbkdf2(String contrasena, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LONGITUD_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo corroborar la contraseña", e);
        }
    }
}
