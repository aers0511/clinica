/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import mapeos.UsuarioEntidad;
import negocio.UsuarioNegocio;

/**
 *
 * @author angel.reyes
 */
public class crearDatos {

    public static void main(String[] args) {

        // Usuario
        String nombreUsuario = "usuario";
        String contrasena = "password";

        UsuarioNegocio usuarioNegocio = new UsuarioNegocio();

        UsuarioEntidad usuario = usuarioNegocio.registrarUsuario(
                nombreUsuario, contrasena);
    }
}
