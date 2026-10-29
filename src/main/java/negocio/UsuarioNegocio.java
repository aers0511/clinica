package negocio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import mapeos.UsuarioEntidad;
import persistencia.DAO;

// La contraseña se guarda como "sal:hash" (PBKDF2).
public class UsuarioNegocio {

    private static final int ITERACIONES = 120000;
    private static final int LONGITUD_BITS = 256;

    private final DAO<UsuarioEntidad> usuarioDAO = new DAO<>(UsuarioEntidad.class);

    public UsuarioEntidad registrarUsuario(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.length() < 8) {
            throw new IllegalArgumentException("Usuario obligatorio y contraseña de al menos 8 caracteres");
        }
        if (!buscarPorUsuario(usuario).isEmpty()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        UsuarioEntidad nuevo = new UsuarioEntidad();
        nuevo.setUsuario(usuario);
        nuevo.setContrasena(hashear(contrasena));
        usuarioDAO.guardar(nuevo);
        return nuevo;
    }

    // Devuelve el usuario si las credenciales son correctas; si no, null
    public UsuarioEntidad iniciarSesion(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return null;
        }
        List<UsuarioEntidad> encontrados = buscarPorUsuario(usuario);
        if (encontrados.isEmpty()) {
            return null;
        }
        UsuarioEntidad encontrado = encontrados.get(0);
        return verificar(contrasena, encontrado.getContrasena()) ? encontrado : null;
    }

    private List<UsuarioEntidad> buscarPorUsuario(String usuario) {
        return usuarioDAO.consultar("SELECT u FROM UsuarioEntidad u WHERE u.usuario = ?1", usuario);
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
            throw new IllegalStateException("No se pudo procesar la contraseña", e);
        }
    }
}