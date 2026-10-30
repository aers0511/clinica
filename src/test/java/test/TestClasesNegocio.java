
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
        System.out.println(" PRUEBAS DE INTEGRACIÓN - LABORATORIO");
        System.out.println("======================================");

        try {
            probarSistema();
        } catch (Exception e) {
            fallidas++;
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n======================================");
        System.out.println("RESULTADOS DE LAS PRUEBAS");
        System.out.println("======================================");
        System.out.println("Correctas: " + correctas);
        System.out.println("Fallidas: " + fallidas);
        System.out.println("Total: " + (correctas + fallidas));
        System.out.println("======================================");
    }

    private static void probarSistema() {

        // Evita conflictos al ejecutar varias veces las pruebas.
        String sufijo = UUID.randomUUID().toString().substring(0, 6);

        UsuarioNegocio usuarioNegocio = new UsuarioNegocio();
        SolicitudNegocio solicitudNegocio = new SolicitudNegocio();
        CatalogoNegocio catalogoNegocio = new CatalogoNegocio();
        ResultadoNegocio resultadoNegocio = new ResultadoNegocio();
        ReporteNegocio reporteNegocio = new ReporteNegocio();

        // ==========================================
        // 1. USUARIO
        // ==========================================

        String nombreUsuario = "recepcion_" + sufijo;
        String contrasena = "Lab2026!";

        UsuarioEntidad usuario = usuarioNegocio.registrarUsuario(
                nombreUsuario,
                contrasena
        );

        verificar(usuario != null && usuario.getId() > 0,
                "Registrar usuario de recepción");

        UsuarioEntidad login = usuarioNegocio.iniciarSesion(
                nombreUsuario,
                contrasena
        );

        verificar(login != null,
                "Iniciar sesión con credenciales correctas");

        UsuarioEntidad loginIncorrecto = usuarioNegocio.iniciarSesion(
                nombreUsuario,
                "ClaveIncorrecta"
        );

        verificar(loginIncorrecto == null,
                "Rechazar contraseña incorrecta");

        // ==========================================
        // 2. CLIENTES
        // ==========================================

        ClienteEntidad cliente1 = solicitudNegocio.registrarCliente(
                "María Fernanda López García",
                LocalDate.of(1998, 5, 14),
                Sexo.FEMENINO,
                TipoSangre.O_POSITIVO
        );

        verificar(cliente1 != null && cliente1.getId() > 0,
                "Registrar paciente María Fernanda López");

        ClienteEntidad cliente2 = solicitudNegocio.registrarCliente(
                "Carlos Alberto Ramírez Torres",
                LocalDate.of(1985, 11, 23),
                Sexo.MASCULINO,
                TipoSangre.A_POSITIVO
        );

        verificar(cliente2 != null && cliente2.getId() > 0,
                "Registrar paciente Carlos Alberto Ramírez");

        ClienteEntidad cliente3 = solicitudNegocio.registrarCliente(
                "Ana Sofía Hernández Castro",
                LocalDate.of(2002, 2, 8),
                Sexo.FEMENINO,
                TipoSangre.O_NEGATIVO
        );

        verificar(cliente3 != null && cliente3.getId() > 0,
                "Registrar paciente Ana Sofía Hernández");

        ClienteEntidad cliente4 = solicitudNegocio.registrarCliente(
                "José Manuel Valenzuela Ruiz",
                LocalDate.of(1972, 8, 19),
                Sexo.MASCULINO,
                TipoSangre.B_POSITIVO
        );

        verificar(cliente4 != null && cliente4.getId() > 0,
                "Registrar paciente José Manuel Valenzuela");

        verificar(solicitudNegocio.listarClientes().stream()
                .anyMatch(c -> c.getId() == cliente1.getId()),
                "Consultar pacientes registrados");

        // ==========================================
        // 3. DOCTORES
        // ==========================================

        DoctorEntidad doctor1 = solicitudNegocio.registrarDoctor(
                "Dra. Laura Patricia Mendoza",
                Sexo.FEMENINO
        );

        verificar(doctor1 != null && doctor1.getId() > 0,
                "Registrar Dra. Laura Patricia Mendoza");

        DoctorEntidad doctor2 = solicitudNegocio.registrarDoctor(
                "Dr. Roberto Carlos Navarro",
                Sexo.MASCULINO
        );

        verificar(doctor2 != null && doctor2.getId() > 0,
                "Registrar Dr. Roberto Carlos Navarro");

        DoctorEntidad doctor3 = solicitudNegocio.registrarDoctor(
                "Dra. Gabriela Martínez Ochoa",
                Sexo.FEMENINO
        );

        verificar(doctor3 != null && doctor3.getId() > 0,
                "Registrar Dra. Gabriela Martínez Ochoa");

        verificar(solicitudNegocio.listarDoctores().stream()
                .anyMatch(d -> d.getId() == doctor1.getId()),
                "Consultar doctores registrados");

        // ==========================================
        // 4. TIPOS DE MUESTRA
        // ==========================================

        TipoMuestraEntidad sangre = catalogoNegocio.registrarTipoMuestra(
                "Sangre venosa"
        );

        TipoMuestraEntidad suero = catalogoNegocio.registrarTipoMuestra(
                "Suero sanguíneo"
        );

        TipoMuestraEntidad orina = catalogoNegocio.registrarTipoMuestra(
                "Orina de chorro medio"
        );

        TipoMuestraEntidad plasma = catalogoNegocio.registrarTipoMuestra(
                "Plasma con EDTA"
        );

        verificar(sangre != null && sangre.getId() > 0,
                "Registrar muestra de sangre venosa");

        verificar(suero != null && suero.getId() > 0,
                "Registrar muestra de suero");

        verificar(orina != null && orina.getId() > 0,
                "Registrar muestra de orina");

        verificar(plasma != null && plasma.getId() > 0,
                "Registrar muestra de plasma");

        // ==========================================
        // 5. ESTUDIOS DE LABORATORIO
        // ==========================================

        EstudioEntidad biometria = catalogoNegocio.registrarEstudio(
                "Biometría hemática completa",
                "Evaluación de células sanguíneas: eritrocitos, leucocitos y plaquetas.",
                sangre.getId()
        );

        EstudioEntidad glucosa = catalogoNegocio.registrarEstudio(
                "Glucosa en sangre",
                "Medición de la concentración de glucosa en sangre en ayuno.",
                suero.getId()
        );

        EstudioEntidad colesterol = catalogoNegocio.registrarEstudio(
                "Perfil de lípidos",
                "Evaluación de colesterol total, HDL, LDL y triglicéridos.",
                suero.getId()
        );

        EstudioEntidad examenOrina = catalogoNegocio.registrarEstudio(
                "Examen general de orina",
                "Evaluación física, química y microscópica de la orina.",
                orina.getId()
        );

        EstudioEntidad creatinina = catalogoNegocio.registrarEstudio(
                "Creatinina sérica",
                "Medición de creatinina para evaluar la función renal.",
                suero.getId()
        );

        verificar(biometria != null && biometria.getId() > 0,
                "Registrar biometría hemática completa");

        verificar(glucosa != null && glucosa.getId() > 0,
                "Registrar glucosa en sangre");

        verificar(colesterol != null && colesterol.getId() > 0,
                "Registrar perfil de lípidos");

        verificar(examenOrina != null && examenOrina.getId() > 0,
                "Registrar examen general de orina");

        verificar(creatinina != null && creatinina.getId() > 0,
                "Registrar creatinina sérica");

        // ==========================================
        // 6. PARÁMETROS DE LABORATORIO
        // ==========================================

        // Biometría hemática.
        ParametroEntidad hemoglobina = catalogoNegocio.registrarParametro(
                "Hemoglobina",
                1,
                "Concentración de hemoglobina en sangre.",
                "g/dL",
                new BigDecimal("12.00"),
                new BigDecimal("16.00"),
                0,
                30,
                biometria.getId()
        );

        ParametroEntidad hematocrito = catalogoNegocio.registrarParametro(
                "Hematocrito",
                2,
                "Porcentaje del volumen sanguíneo ocupado por eritrocitos.",
                "%",
                new BigDecimal("36.00"),
                new BigDecimal("46.00"),
                0,
                100,
                biometria.getId()
        );

        ParametroEntidad leucocitos = catalogoNegocio.registrarParametro(
                "Leucocitos",
                3,
                "Recuento de glóbulos blancos.",
                "10^3/uL",
                new BigDecimal("4.50"),
                new BigDecimal("11.00"),
                0,
                100,
                biometria.getId()
        );

        ParametroEntidad plaquetas = catalogoNegocio.registrarParametro(
                "Plaquetas",
                4,
                "Recuento de plaquetas en sangre.",
                "10^3/uL",
                new BigDecimal("150.00"),
                new BigDecimal("450.00"),
                0,
                1000,
                biometria.getId()
        );

        // Glucosa.
        ParametroEntidad glucosaAyuno = catalogoNegocio.registrarParametro(
                "Glucosa en ayuno",
                1,
                "Concentración de glucosa después de un ayuno.",
                "mg/dL",
                new BigDecimal("70.00"),
                new BigDecimal("99.00"),
                0,
                600,
                glucosa.getId()
        );

        // Perfil de lípidos.
        ParametroEntidad colesterolTotal = catalogoNegocio.registrarParametro(
                "Colesterol total",
                1,
                "Concentración de colesterol total en sangre.",
                "mg/dL",
                new BigDecimal("0.00"),
                new BigDecimal("200.00"),
                0,
                1000,
                colesterol.getId()
        );

        ParametroEntidad hdl = catalogoNegocio.registrarParametro(
                "Colesterol HDL",
                2,
                "Concentración de lipoproteínas de alta densidad.",
                "mg/dL",
                new BigDecimal("40.00"),
                new BigDecimal("100.00"),
                0,
                300,
                colesterol.getId()
        );

        ParametroEntidad ldl = catalogoNegocio.registrarParametro(
                "Colesterol LDL",
                3,
                "Concentración de lipoproteínas de baja densidad.",
                "mg/dL",
                new BigDecimal("0.00"),
                new BigDecimal("100.00"),
                0,
                500,
                colesterol.getId()
        );

        ParametroEntidad trigliceridos = catalogoNegocio.registrarParametro(
                "Triglicéridos",
                4,
                "Concentración de triglicéridos en sangre.",
                "mg/dL",
                new BigDecimal("0.00"),
                new BigDecimal("150.00"),
                0,
                1000,
                colesterol.getId()
        );

        // Creatinina.
        ParametroEntidad creatininaSerica = catalogoNegocio.registrarParametro(
                "Creatinina sérica",
                1,
                "Concentración de creatinina en sangre.",
                "mg/dL",
                new BigDecimal("0.60"),
                new BigDecimal("1.20"),
                0,
                30,
                creatinina.getId()
        );

        verificar(hemoglobina != null && hemoglobina.getId() > 0,
                "Registrar parámetro hemoglobina");

        verificar(hematocrito != null && hematocrito.getId() > 0,
                "Registrar parámetro hematocrito");

        verificar(leucocitos != null && leucocitos.getId() > 0,
                "Registrar parámetro leucocitos");

        verificar(plaquetas != null && plaquetas.getId() > 0,
                "Registrar parámetro plaquetas");

        verificar(glucosaAyuno != null && glucosaAyuno.getId() > 0,
                "Registrar parámetro glucosa");

        verificar(colesterolTotal != null && colesterolTotal.getId() > 0,
                "Registrar parámetro colesterol total");

        verificar(hdl != null && hdl.getId() > 0,
                "Registrar parámetro HDL");

        verificar(ldl != null && ldl.getId() > 0,
                "Registrar parámetro LDL");

        verificar(trigliceridos != null && trigliceridos.getId() > 0,
                "Registrar parámetro triglicéridos");

        verificar(creatininaSerica != null && creatininaSerica.getId() > 0,
                "Registrar parámetro creatinina");

        // ==========================================
        // 7. CONSULTAS DEL CATÁLOGO
        // ==========================================

        verificar(catalogoNegocio.listarTiposMuestra().size() >= 4,
                "Consultar tipos de muestra");

        verificar(catalogoNegocio.listarEstudios().size() >= 5,
                "Consultar estudios registrados");

        verificar(catalogoNegocio.listarParametrosPorEstudio(
                biometria.getId()).size() >= 4,
                "Consultar parámetros de biometría");

        verificar(catalogoNegocio.listarParametrosPorEstudio(
                colesterol.getId()).size() >= 4,
                "Consultar parámetros de perfil de lípidos");

        // ==========================================
        // 8. REGISTRO DE SOLICITUDES
        // ==========================================

        PruebaEntidad prueba1 = solicitudNegocio.registrarPrueba(
                cliente1.getId(),
                doctor1.getId(),
                Arrays.asList(
                        biometria.getId(),
                        glucosa.getId(),
                        colesterol.getId()
                )
        );

        PruebaEntidad prueba2 = solicitudNegocio.registrarPrueba(
                cliente2.getId(),
                doctor2.getId(),
                Arrays.asList(
                        glucosa.getId(),
                        creatinina.getId()
                )
        );

        PruebaEntidad prueba3 = solicitudNegocio.registrarPrueba(
                cliente3.getId(),
                doctor3.getId(),
                Arrays.asList(
                        biometria.getId(),
                        examenOrina.getId()
                )
        );

        PruebaEntidad prueba4 = solicitudNegocio.registrarPrueba(
                cliente4.getId(),
                doctor1.getId(),
                Arrays.asList(
                        colesterol.getId(),
                        creatinina.getId()
                )
        );

        verificar(prueba1 != null && prueba1.getId() > 0,
                "Registrar solicitud de María Fernanda");

        verificar(prueba2 != null && prueba2.getId() > 0,
                "Registrar solicitud de Carlos Alberto");

        verificar(prueba3 != null && prueba3.getId() > 0,
                "Registrar solicitud de Ana Sofía");

        verificar(prueba4 != null && prueba4.getId() > 0,
                "Registrar solicitud de José Manuel");

        verificar(prueba1.getFolio() != null
                && !prueba1.getFolio().isBlank(),
                "Generar folio de solicitud");

        verificar(solicitudNegocio.listarPruebasPorCliente(
                cliente1.getId()).stream()
                .anyMatch(p -> p.getId() == prueba1.getId()),
                "Consultar solicitudes por paciente");

        verificar(solicitudNegocio.listarPruebasPorDoctor(
                doctor1.getId()).stream()
                .anyMatch(p -> p.getId() == prueba1.getId()),
                "Consultar solicitudes por médico");

        // ==========================================
        // 9. CAPTURA DE RESULTADOS
        // ==========================================

        // Resultados de María Fernanda.
        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                hemoglobina,
                "13.80",
                "Dentro del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                hematocrito,
                "41.20",
                "Dentro del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                leucocitos,
                "7.50",
                "Dentro del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                plaquetas,
                "285.00",
                "Dentro del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                glucosaAyuno,
                "92.00",
                "Dentro del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                colesterolTotal,
                "185.00",
                "Dentro del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                hdl,
                "58.00",
                "Dentro del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                ldl,
                "105.00",
                "Resultado de ejemplo"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba1,
                trigliceridos,
                "110.00",
                "Dentro del rango de referencia"
        );

        // Resultados de Carlos Alberto.
        registrarYVerificarResultado(
                resultadoNegocio,
                prueba2,
                glucosaAyuno,
                "108.00",
                "Resultado de ejemplo fuera del rango de referencia"
        );

        registrarYVerificarResultado(
                resultadoNegocio,
                prueba2,
                creatininaSerica,
                "0.95",
                "Dentro del rango de referencia"
        );

        // ==========================================
        // 10. VALIDACIÓN DE RESULTADOS
        // ==========================================

        verificar(!resultadoNegocio.listarResultadosPorPrueba(
                prueba1.getId()).isEmpty(),
                "Consultar resultados de una solicitud");

        verificar(resultadoNegocio.parametrosFaltantes(
                prueba2.getId()).isEmpty(),
                "Verificar que Carlos tenga todos sus resultados");

        verificar(!resultadoNegocio.parametrosFaltantes(
                prueba3.getId()).isEmpty(),
                "Detectar parámetros pendientes de captura");

        verificar(resultadoNegocio.parametrosAplicables(
                prueba1.getId(),
                biometria.getId()).size() == 4,
                "Consultar parámetros aplicables a biometría");

        // ==========================================
        // 11. GENERACIÓN DE REPORTES
        // ==========================================

        String reporte = reporteNegocio.generarReporte(
                prueba1.getId()
        );

        verificar(reporte != null
                && reporte.contains(prueba1.getFolio()),
                "Generar reporte de María Fernanda");

        verificar(reporte.contains("Hemoglobina"),
                "Incluir hemoglobina en el reporte");

        verificar(reporte.contains("13.80"),
                "Incluir resultado de hemoglobina");

        String reporte2 = reporteNegocio.generarReporte(
                prueba2.getId()
        );

        verificar(reporte2 != null
                && reporte2.contains(prueba2.getFolio()),
                "Generar reporte de Carlos Alberto");

        // ==========================================
        // 12. RESUMEN
        // ==========================================

        System.out.println("\n======================================");
        System.out.println("REGISTROS CREADOS");
        System.out.println("======================================");

        System.out.println("Usuario: " + usuario.getId());

        System.out.println("\nPACIENTES");
        System.out.println("María Fernanda: " + cliente1.getId());
        System.out.println("Carlos Alberto: " + cliente2.getId());
        System.out.println("Ana Sofía: " + cliente3.getId());
        System.out.println("José Manuel: " + cliente4.getId());

        System.out.println("\nDOCTORES");
        System.out.println("Laura Mendoza: " + doctor1.getId());
        System.out.println("Roberto Navarro: " + doctor2.getId());
        System.out.println("Gabriela Martínez: " + doctor3.getId());

        System.out.println("\nSOLICITUDES");
        System.out.println("María Fernanda: " + prueba1.getFolio());
        System.out.println("Carlos Alberto: " + prueba2.getFolio());
        System.out.println("Ana Sofía: " + prueba3.getFolio());
        System.out.println("José Manuel: " + prueba4.getFolio());
    }

    private static void registrarYVerificarResultado(
            ResultadoNegocio resultadoNegocio,
            PruebaEntidad prueba,
            ParametroEntidad parametro,
            String valor,
            String observacion) {

        ResultadoEntidad resultado = resultadoNegocio.registrarResultado(
                prueba.getId(),
                parametro.getId(),
                new BigDecimal(valor),
                observacion
        );

        verificar(resultado != null && resultado.getId() > 0,
                "Registrar " + parametro.getNombre()
                + " para folio " + prueba.getFolio());
    }

    private static void verificar(
            boolean condicion,
            String descripcion) {

        if (condicion) {
            correctas++;
            System.out.println("[OK] " + descripcion);
        } else {
            fallidas++;
            System.out.println("[FALLO] " + descripcion);
        }
    }
}
