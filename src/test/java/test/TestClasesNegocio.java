
package test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import mapeos.ClienteEntidad;
import mapeos.DoctorEntidad;
import mapeos.EstudioEntidad;
import mapeos.ParametroEntidad;
import mapeos.PruebaEntidad;
import mapeos.ResultadoEntidad;
import mapeos.TipoMuestraEntidad;
import mapeos.TipoSangre;
import mapeos.Sexo;
import mapeos.UsuarioEntidad;

import negocio.CatalogoNegocio;
import negocio.ReporteNegocio;
import negocio.ResultadoNegocio;
import negocio.SolicitudNegocio;
import negocio.UsuarioNegocio;

public class TestClasesNegocio {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println(" PRUEBAS DE INTEGRACIÓN - CLÍNICA");
        System.out.println("======================================");

        try {
            probarSistema();
        } catch (Exception e) {
            fallidas++;
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n======================================");
        System.out.println("Correctas: " + correctas);
        System.out.println("Fallidas: " + fallidas);
        System.out.println("======================================");
    }

    private static void probarSistema() {

        // Se usa un sufijo para que los datos no se repitan.
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        UsuarioNegocio usuarioNegocio = new UsuarioNegocio();
        SolicitudNegocio solicitudNegocio = new SolicitudNegocio();
        CatalogoNegocio catalogoNegocio = new CatalogoNegocio();
        ResultadoNegocio resultadoNegocio = new ResultadoNegocio();
        ReporteNegocio reporteNegocio = new ReporteNegocio();

        // Usuario
        String nombreUsuario = "test_" + sufijo;
        String contrasena = "Prueba123!";

        UsuarioEntidad usuario = usuarioNegocio.registrarUsuario(
                nombreUsuario, contrasena);

        verificar(usuario != null && usuario.getId() > 0,
                "UsuarioNegocio: registrarUsuario");

        UsuarioEntidad login = usuarioNegocio.iniciarSesion(
                nombreUsuario, contrasena);

        verificar(login != null,
                "UsuarioNegocio: iniciarSesion con contraseña correcta");

        UsuarioEntidad loginIncorrecto = usuarioNegocio.iniciarSesion(
                nombreUsuario, "ContrasenaIncorrecta");

        verificar(loginIncorrecto == null,
                "UsuarioNegocio: rechazar contraseña incorrecta");

        // Cliente y doctor
        ClienteEntidad cliente = solicitudNegocio.registrarCliente(
                "Cliente de prueba " + sufijo,
                LocalDate.now().minusYears(30),
                Sexo.MASCULINO,
                TipoSangre.O_POSITIVO
        );

        verificar(cliente != null && cliente.getId() > 0,
                "SolicitudNegocio: registrarCliente");

        DoctorEntidad doctor = solicitudNegocio.registrarDoctor(
                "Doctor de prueba " + sufijo,
                Sexo.FEMENINO
        );

        verificar(doctor != null && doctor.getId() > 0,
                "SolicitudNegocio: registrarDoctor");

        verificar(solicitudNegocio.listarClientes().stream()
                .anyMatch(c -> c.getId() == cliente.getId()),
                "SolicitudNegocio: listarClientes");

        verificar(solicitudNegocio.listarDoctores().stream()
                .anyMatch(d -> d.getId() == doctor.getId()),
                "SolicitudNegocio: listarDoctores");

        // Catálogo
        TipoMuestraEntidad tipoMuestra =
                catalogoNegocio.registrarTipoMuestra(
                        "Muestra de prueba " + sufijo);

        verificar(tipoMuestra != null && tipoMuestra.getId() > 0,
                "CatalogoNegocio: registrarTipoMuestra");

        EstudioEntidad estudio = catalogoNegocio.registrarEstudio(
                "Estudio de prueba " + sufijo,
                "Estudio de prueba",
                tipoMuestra.getId()
        );

        verificar(estudio != null && estudio.getId() > 0,
                "CatalogoNegocio: registrarEstudio");

        ParametroEntidad parametro = catalogoNegocio.registrarParametro(
                "Parametro de prueba " + sufijo,
                1,
                "Parametro de prueba",
                "mg/dL",
                new BigDecimal("70.00"),
                new BigDecimal("100.00"),
                0,
                120,
                estudio.getId()
        );

        verificar(parametro != null && parametro.getId() > 0,
                "CatalogoNegocio: registrarParametro");

        verificar(catalogoNegocio.listarTiposMuestra().stream()
                .anyMatch(t -> t.getId() == tipoMuestra.getId()),
                "CatalogoNegocio: listarTiposMuestra");

        verificar(catalogoNegocio.listarEstudios().stream()
                .anyMatch(e -> e.getId() == estudio.getId()),
                "CatalogoNegocio: listarEstudios");

        verificar(catalogoNegocio.listarParametrosPorEstudio(estudio.getId())
                .stream().anyMatch(p -> p.getId() == parametro.getId()),
                "CatalogoNegocio: listarParametrosPorEstudio");

        // Prueba de laboratorio
        PruebaEntidad prueba = solicitudNegocio.registrarPrueba(
                cliente.getId(),
                doctor.getId(),
                Arrays.asList(estudio.getId())
        );

        verificar(prueba != null && prueba.getId() > 0
                && prueba.getFolio() != null
                && !prueba.getFolio().isBlank(),
                "SolicitudNegocio: registrarPrueba");

        verificar(solicitudNegocio.listarPruebasPorCliente(cliente.getId())
                .stream().anyMatch(p -> p.getId() == prueba.getId()),
                "SolicitudNegocio: listarPruebasPorCliente");

        verificar(solicitudNegocio.listarPruebasPorDoctor(doctor.getId())
                .stream().anyMatch(p -> p.getId() == prueba.getId()),
                "SolicitudNegocio: listarPruebasPorDoctor");

        // Revisar los parámetros de la prueba.
        verificar(resultadoNegocio.parametrosAplicables(
                prueba.getId(), estudio.getId()).stream()
                .anyMatch(p -> p.getId() == parametro.getId()),
                "ResultadoNegocio: parametrosAplicables");

        verificar(resultadoNegocio.parametrosFaltantes(prueba.getId())
                .stream().anyMatch(p -> p.getId() == parametro.getId()),
                "ResultadoNegocio: parametrosFaltantes antes de capturar");

        // Registrar resultado
        ResultadoEntidad resultado = resultadoNegocio.registrarResultado(
                prueba.getId(),
                parametro.getId(),
                new BigDecimal("85.50"),
                "Resultado de prueba"
        );

        verificar(resultado != null && resultado.getId() > 0,
                "ResultadoNegocio: registrarResultado");

        verificar(resultadoNegocio.listarResultadosPorPrueba(prueba.getId())
                .stream().anyMatch(r -> r.getId() == resultado.getId()),
                "ResultadoNegocio: listarResultadosPorPrueba");

        verificar(resultadoNegocio.parametrosFaltantes(prueba.getId()).isEmpty(),
                "ResultadoNegocio: sin parámetros faltantes tras capturar");

        // Reporte
        String reporte = reporteNegocio.generarReporte(prueba.getId());

        verificar(reporte != null
                && reporte.contains(prueba.getFolio())
                && reporte.contains("85.50"),
                "ReporteNegocio: generarReporte");

        System.out.println("\nIDs creados:");
        System.out.println("Usuario: " + usuario.getId());
        System.out.println("Cliente: " + cliente.getId());
        System.out.println("Doctor: " + doctor.getId());
        System.out.println("Tipo de muestra: " + tipoMuestra.getId());
        System.out.println("Estudio: " + estudio.getId());
        System.out.println("Parametro: " + parametro.getId());
        System.out.println("Prueba: " + prueba.getId()
                + " (" + prueba.getFolio() + ")");
        System.out.println("Resultado: " + resultado.getId());
    }

    private static void verificar(boolean condicion, String descripcion) {

        if (condicion) {
            correctas++;
            System.out.println("[OK] " + descripcion);
        } else {
            fallidas++;
            System.out.println("[FALLO] " + descripcion);
        }
    }
}
