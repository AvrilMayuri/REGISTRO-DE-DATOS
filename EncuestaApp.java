import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class EncuestaApp extends JFrame {
    private final Color COLOR_VINO = Color.decode("#780000");
    private JComboBox<String>[] comboBoxes = new JComboBox[14];
    private DefaultTableModel tableModel;
    private JTable table;
    private final String RUTA_DESCARGAS = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;

    public EncuestaApp() {
        setTitle("Encuesta UTP - Registro de Datos");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);

        // --- Panel Izquierdo (Formulario) ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        formPanel.setBackground(Color.WHITE);

        String[] preguntas = {
            "Item 1-1: Accesibilidad pagos", "Item 2-1: % ingresos", "Item 3-1: Dificultad pago", 
            "Item 4-1: Apoyo económico", "Item 5-1: Calificación pensión", "Item 6-1: Aumento costos", 
            "Item 7-1: Satisfacción costos", "Item 1-2: Preparación docente", "Item 2-2: Facilidad aprendizaje", 
            "Item 3-2: Actualización contenidos", "Item 4-2: Retroalimentación", "Item 5-2: Calidad enseñanza", 
            "Item 6-2: Satisfacción calidad", "Item 7-2: Recomendación UTP"
        };

        for (int i = 0; i < 14; i++) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.setBackground(Color.WHITE);
            JLabel lbl = new JLabel(preguntas[i]);
            lbl.setPreferredSize(new Dimension(220, 25));
            comboBoxes[i] = new JComboBox<>(new String[]{"1", "2", "3", "4", "5"});
            p.add(lbl);
            p.add(comboBoxes[i]);
            formPanel.add(p);
        }

        // --- Botones Corregidos ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        btnPanel.setBackground(Color.WHITE);
        
        // Colores: Principal (Vino), Borrar (Rojo), Exportar (Vino), Abrir (Verde)
        JButton btnRegistrar = crearBotonEstilizado("Registrar", COLOR_VINO);
        JButton btnBorrar = crearBotonEstilizado("Borrar Fila", Color.RED);
        JButton btnExportar = crearBotonEstilizado("Exportar CSV", COLOR_VINO);
        JButton btnAbrir = crearBotonEstilizado("Abrir Descargas", new Color(0, 150, 70));

        btnRegistrar.addActionListener(e -> registrarDatos());
        btnBorrar.addActionListener(e -> borrarFila());
        btnExportar.addActionListener(e -> exportarCSV());
        btnAbrir.addActionListener(e -> abrirCarpetaDescargas());

        btnPanel.add(btnRegistrar);
        btnPanel.add(btnBorrar);
        btnPanel.add(btnExportar);
        btnPanel.add(btnAbrir);
        formPanel.add(btnPanel);

        // --- Tabla con Cabecera en Rojo Vino ---
        String[] col = new String[14];
        for(int i=0; i<7; i++) col[i] = "Item"+(i+1)+"-1";
        for(int i=7; i<14; i++) col[i] = "Item"+(i-6)+"-2";
        
        tableModel = new DefaultTableModel(col, 0);
        table = new JTable(tableModel);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_VINO);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(formPanel), new JScrollPane(table));
        splitPane.setDividerLocation(500); 
        add(splitPane);
        
        setLocationRelativeTo(null);
    }

    private JButton crearBotonEstilizado(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true); // OBLIGATORIO para mostrar el fondo
        btn.setOpaque(true);           // OBLIGATORIO para mostrar el fondo
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(130, 35));
        return btn;
    }

    private void registrarDatos() {
        String[] fila = new String[14];
        for (int i = 0; i < 14; i++) fila[i] = (String) comboBoxes[i].getSelectedItem();
        tableModel.addRow(fila);
    }

    private void borrarFila() {
        int row = table.getSelectedRow();
        if (row != -1) tableModel.removeRow(row);
    }

    private void exportarCSV() {
        String archivoPath = RUTA_DESCARGAS + "datos_encuesta.csv";
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoPath))) {
            for (int i = 0; i < 14; i++) pw.print(tableModel.getColumnName(i) + (i == 13 ? "" : ","));
            pw.println();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < 14; j++) pw.print(tableModel.getValueAt(i, j) + (j == 13 ? "" : ","));
                pw.println();
            }
            abrirCarpetaDescargas();
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Error al guardar"); }
    }

    private void abrirCarpetaDescargas() {
        try {
            File folder = new File(RUTA_DESCARGAS);
            if (folder.exists()) {
                Desktop.getDesktop().open(folder);
            } else {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("mac")) Runtime.getRuntime().exec("open " + RUTA_DESCARGAS);
                else if (os.contains("win")) Runtime.getRuntime().exec("explorer.exe " + RUTA_DESCARGAS);
            }
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "No se pudo abrir la carpeta"); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EncuestaApp().setVisible(true));
    }
}