/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package views;

/**
 *
 * @author eldp7
 */
public class PDF extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PDF.class.getName());

    /**
     * Creates new form PDF
     */
    public PDF() {
        initComponents();
        vista.setEditable(false);
        vista.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        JscrlpVisualizacionPDF.setViewportView(vista);
        JbtnBuscarPDF.addActionListener(e -> buscarReporte());
        JlblGenerarPDFPDF.addActionListener(e -> guardarPDF());
        JbtnImprimirPDF.addActionListener(e -> imprimir());
        getRootPane().registerKeyboardAction(e -> {
            new MenuClinica().setVisible(true);
            dispose();
        }, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
        javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
    }


    private final javax.swing.JTextArea vista = new javax.swing.JTextArea();
    private String reporteActual;

    private void buscarReporte() {
        try {
            String folio = JtxtfFolioPDF.getText().trim();
            if (folio.isEmpty()) throw new IllegalArgumentException("Ingrese un folio");
            mapeos.PruebaEntidad prueba = new negocio.SolicitudNegocio().buscarPrueba(folio);
            reporteActual = new negocio.ReporteNegocio().generarReporte(prueba.getId());
            vista.setText(reporteActual);
            vista.setCaretPosition(0);
        } catch (RuntimeException e) {
            reporteActual = null;
            vista.setText("");
            javax.swing.JOptionPane.showMessageDialog(this, "No se pudo generar el reporte: " + e.getMessage());
        }
    }

    private void guardarPDF() {
        if (reporteActual == null) {
            buscarReporte();
            if (reporteActual == null) return;
        }
        javax.swing.JFileChooser selector = new javax.swing.JFileChooser();
        selector.setSelectedFile(new java.io.File("Reporte-" + JtxtfFolioPDF.getText().trim() + ".pdf"));
        if (selector.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        java.io.File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new java.io.File(archivo.getAbsolutePath() + ".pdf");
        }
        if (archivo.exists() && javax.swing.JOptionPane.showConfirmDialog(this,
                "El archivo existe. Desea reemplazarlo?", "Confirmar",
                javax.swing.JOptionPane.YES_NO_OPTION) != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            escribirPDF(archivo, reporteActual);
            javax.swing.JOptionPane.showMessageDialog(this, "PDF guardado en: " + archivo);
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al guardar PDF: " + e.getMessage());
        }
    }

    private void imprimir() {
        if (reporteActual == null) {
            buscarReporte();
            if (reporteActual == null) return;
        }
        try {
            vista.print();
        } catch (java.awt.print.PrinterException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al imprimir: " + e.getMessage());
        }
    }

    // Genera un PDF de texto sin dependencias externas.
    private void escribirPDF(java.io.File archivo, String texto) throws java.io.IOException {
        java.util.List<String> lineas = new java.util.ArrayList<>();
        for (String linea : texto.replace("\r", "").split("\n", -1)) {
            // Helvetica de PDF con caracteres basicos.
            linea = java.text.Normalizer.normalize(linea, java.text.Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "").replaceAll("[^\\x20-\\x7E]", "?");
            while (linea.length() > 88) {
                int corte = linea.lastIndexOf(' ', 88);
                if (corte < 1) corte = 88;
                lineas.add(linea.substring(0, corte));
                linea = linea.substring(corte).trim();
            }
            lineas.add(linea);
        }
        int paginas = Math.max(1, (lineas.size() + 48) / 49);
        int objetos = 3 + paginas * 2;
        java.io.ByteArrayOutputStream salida = new java.io.ByteArrayOutputStream();
        java.util.List<Integer> posiciones = new java.util.ArrayList<>();
        posiciones.add(0);
        salida.write("%PDF-1.4\n".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        for (int numero = 1; numero <= objetos; numero++) {
            posiciones.add(salida.size());
            String contenido;
            if (numero == 1) {
                contenido = "<< /Type /Catalog /Pages 2 0 R >>";
            } else if (numero == 2) {
                StringBuilder hijos = new StringBuilder();
                for (int pagina = 0; pagina < paginas; pagina++) hijos.append(4 + pagina * 2).append(" 0 R ");
                contenido = "<< /Type /Pages /Kids [" + hijos + "] /Count " + paginas + " >>";
            } else if (numero == 3) {
                contenido = "<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>";
            } else if (numero % 2 == 0) {
                contenido = "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                    + "/Resources << /Font << /F1 3 0 R >> >> /Contents " + (numero + 1) + " 0 R >>";
            } else {
                int pagina = (numero - 5) / 2;
                StringBuilder textoPagina = new StringBuilder("BT /F1 9 Tf 40 752 Td 14 TL\n");
                int inicio = pagina * 49;
                for (int i = inicio; i < Math.min(inicio + 49, lineas.size()); i++) {
                    String linea = lineas.get(i).replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
                    textoPagina.append("(").append(linea).append(") Tj T*\n");
                }
                textoPagina.append("ET\n");
                byte[] datos = textoPagina.toString().getBytes(java.nio.charset.StandardCharsets.US_ASCII);
                contenido = "<< /Length " + datos.length + " >>\nstream\n" + textoPagina + "endstream";
            }
            salida.write((numero + " 0 obj\n" + contenido + "\nendobj\n")
                    .getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        }
        int inicioTabla = salida.size();
        salida.write(("xref\n0 " + (objetos + 1) + "\n0000000000 65535 f \n")
                .getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        for (int i = 1; i <= objetos; i++) {
            salida.write(String.format(java.util.Locale.ROOT, "%010d 00000 n \n", posiciones.get(i))
                    .getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        }
        salida.write(("trailer\n<< /Size " + (objetos + 1) + " /Root 1 0 R >>\nstartxref\n"
                + inicioTabla + "\n%%EOF\n").getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        java.nio.file.Files.write(archivo.toPath(), salida.toByteArray());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JpnlSuperiorPDF = new javax.swing.JPanel();
        JlblTitulopdf = new javax.swing.JLabel();
        JlblLaboratorioPDF = new javax.swing.JLabel();
        JpnlInferiorPDF = new javax.swing.JPanel();
        JpnlIzquieroPDF = new javax.swing.JPanel();
        JlblReportePDF = new javax.swing.JLabel();
        JlblFolioPDF = new javax.swing.JLabel();
        JtxtfFolioPDF = new javax.swing.JTextField();
        JbtnBuscarPDF = new javax.swing.JButton();
        JlblGenerarPDFPDF = new javax.swing.JButton();
        JbtnImprimirPDF = new javax.swing.JButton();
        JpnlVisualizacionPDF = new javax.swing.JPanel();
        JscrlpVisualizacionPDF = new javax.swing.JScrollPane();
        JlblVisualizacionPDF = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JpnlSuperiorPDF.setBackground(new java.awt.Color(255, 242, 242));

        JlblTitulopdf.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        JlblTitulopdf.setText("Salud Total");

        JlblLaboratorioPDF.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        JlblLaboratorioPDF.setText("Laboratorio Clinico");

        javax.swing.GroupLayout JpnlSuperiorPDFLayout = new javax.swing.GroupLayout(JpnlSuperiorPDF);
        JpnlSuperiorPDF.setLayout(JpnlSuperiorPDFLayout);
        JpnlSuperiorPDFLayout.setHorizontalGroup(
            JpnlSuperiorPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JpnlSuperiorPDFLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(JlblTitulopdf, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(524, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JpnlSuperiorPDFLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(JlblLaboratorioPDF)
                .addGap(54, 54, 54))
        );
        JpnlSuperiorPDFLayout.setVerticalGroup(
            JpnlSuperiorPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JpnlSuperiorPDFLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(JlblTitulopdf, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(JlblLaboratorioPDF)
                .addContainerGap())
        );

        JpnlInferiorPDF.setBackground(new java.awt.Color(173, 173, 173));

        JlblReportePDF.setText("Reporte");

        JlblFolioPDF.setText("Folio de la prueba:");

        JbtnBuscarPDF.setText("Buscar");

        JlblGenerarPDFPDF.setText("Generar  PDF");

        JbtnImprimirPDF.setText("Imprimir");

        javax.swing.GroupLayout JpnlIzquieroPDFLayout = new javax.swing.GroupLayout(JpnlIzquieroPDF);
        JpnlIzquieroPDF.setLayout(JpnlIzquieroPDFLayout);
        JpnlIzquieroPDFLayout.setHorizontalGroup(
            JpnlIzquieroPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JpnlIzquieroPDFLayout.createSequentialGroup()
                .addGroup(JpnlIzquieroPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, JpnlIzquieroPDFLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(JpnlIzquieroPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(JbtnBuscarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(JpnlIzquieroPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(JlblFolioPDF)
                                .addComponent(JlblReportePDF)
                                .addComponent(JtxtfFolioPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(JpnlIzquieroPDFLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(JpnlIzquieroPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(JlblGenerarPDFPDF, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                            .addComponent(JbtnImprimirPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        JpnlIzquieroPDFLayout.setVerticalGroup(
            JpnlIzquieroPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JpnlIzquieroPDFLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(JlblReportePDF)
                .addGap(18, 18, 18)
                .addComponent(JlblFolioPDF)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JtxtfFolioPDF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(JbtnBuscarPDF)
                .addGap(28, 28, 28)
                .addComponent(JlblGenerarPDFPDF)
                .addGap(28, 28, 28)
                .addComponent(JbtnImprimirPDF)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        JscrlpVisualizacionPDF.setViewportView(JlblVisualizacionPDF);

        javax.swing.GroupLayout JpnlVisualizacionPDFLayout = new javax.swing.GroupLayout(JpnlVisualizacionPDF);
        JpnlVisualizacionPDF.setLayout(JpnlVisualizacionPDFLayout);
        JpnlVisualizacionPDFLayout.setHorizontalGroup(
            JpnlVisualizacionPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(JscrlpVisualizacionPDF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
        );
        JpnlVisualizacionPDFLayout.setVerticalGroup(
            JpnlVisualizacionPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(JscrlpVisualizacionPDF)
        );

        javax.swing.GroupLayout JpnlInferiorPDFLayout = new javax.swing.GroupLayout(JpnlInferiorPDF);
        JpnlInferiorPDF.setLayout(JpnlInferiorPDFLayout);
        JpnlInferiorPDFLayout.setHorizontalGroup(
            JpnlInferiorPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JpnlInferiorPDFLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(JpnlIzquieroPDF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150)
                .addComponent(JpnlVisualizacionPDF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JpnlInferiorPDFLayout.setVerticalGroup(
            JpnlInferiorPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JpnlInferiorPDFLayout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(JpnlInferiorPDFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(JpnlVisualizacionPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(JpnlIzquieroPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(JpnlSuperiorPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(JpnlInferiorPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(JpnlSuperiorPDF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JpnlInferiorPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new PDF().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton JbtnBuscarPDF;
    private javax.swing.JButton JbtnImprimirPDF;
    private javax.swing.JLabel JlblFolioPDF;
    private javax.swing.JButton JlblGenerarPDFPDF;
    private javax.swing.JLabel JlblLaboratorioPDF;
    private javax.swing.JLabel JlblReportePDF;
    private javax.swing.JLabel JlblTitulopdf;
    private javax.swing.JLabel JlblVisualizacionPDF;
    private javax.swing.JPanel JpnlInferiorPDF;
    private javax.swing.JPanel JpnlIzquieroPDF;
    private javax.swing.JPanel JpnlSuperiorPDF;
    private javax.swing.JPanel JpnlVisualizacionPDF;
    private javax.swing.JScrollPane JscrlpVisualizacionPDF;
    private javax.swing.JTextField JtxtfFolioPDF;
    // End of variables declaration//GEN-END:variables
}
