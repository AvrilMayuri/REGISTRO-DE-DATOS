import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class EncuestaApp extends JFrame {
    private final Color VINO = new Color(139, 0, 0);
    private final Color AZUL_OSCURO = new Color(0, 0, 139);
    private final Color GRIS_AZULADO = new Color(60, 70, 80);
    private final Color NEGRO = Color.BLACK;

    private JComboBox<String>[] comboBoxes = new JComboBox[14];
    private DefaultTableModel tableModel;
    private JTable table;

    private String[] cabeceras = {"Item 1-1", "Item 1-2", "Item 1-3", "Item 1-4", "Item 2-1", "Item 2-2", "Item 2-3", "Item 2-4", "Item 2-5", "Item 2-6", "Item 2-7", "Item 2-8", "Item 2-9", "Item 2-10"};
    
    private String[] preguntas = {
        "¿Accesibilidad pagos?", "¿% ingresos?", "¿Frecuencia dificultad?", "¿Apoyo económico?", 
        "¿Calidad pensión?", "¿Aumento costos?", "¿Satisfacción costos?", "¿Preparación docentes?", 
        "¿Métodos enseñanza?", "¿Actualización contenidos?", "¿Retroalimentación?", "¿Calidad enseñanza?", 
        "¿Satisfacción carrera?", "¿Recomendarías UTP?"
    };

    private String[][] etiquetasOpciones = {
        {"1: Nada accesibles", "2: Poco accesibles", "3: Accesibles", "4: Muy accesibles"},
        {"1: < 25%", "2: 25% - 50%", "3: 51% - 75%", "4: > 75%"},
        {"1: Nunca", "2: Rara vez", "3: A veces", "4: Frecuentemente", "5: Siempre"},
        {"1: Sí", "2: No"},
        {"1: Muy bajo", "2: Bajo", "3: Moderado", "4: Alto", "5: Muy alto"},
        {"1: Sí", "2: No", "3: No seguro"},
        {"1: Muy insatisfecho", "2: Insatisfecho", "3: Neutral", "4: Satisfecho", "5: Muy satisfecho"},
        {"1: Nada preparados", "2: Poco preparados", "3: Moderado", "4: Bien preparados", "5: Muy bien preparados"},
        {"1: Nunca", "2: Rara vez", "3: A veces", "4: Frecuentemente", "5: Siempre"},
        {"1: Nada actualizados", "2: Poco actualizados", "3: Moderado", "4: Actualizados", "5: Muy actualizados"},
        {"1: Nunca", "2: Rara vez", "3: A veces", "4: Frecuentemente", "5: Siempre"},
        {"1: Muy mala", "2: Mala", "3: Regular", "4: Buena", "5: Muy buena"},
        {"1: Muy insatisfecho", "2: Insatisfecho", "3: Neutral", "4: Satisfecho", "5: Muy satisfecho"},
        {"1: Sí", "2: No", "3: Tal vez"}
    };

    public EncuestaApp() {
        setTitle("Sistema de Gestión de Encuestas - UTP");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(GRIS_AZULADO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        // Título en la parte izquierda
        JLabel tituloIzq = new JLabel("REGISTRO DE DATOS DE LA ENCUESTA");
        tituloIzq.setForeground(Color.WHITE);
        tituloIzq.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(tituloIzq, gbc);

        for (int i = 0; i < 14; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
            JLabel lbl = new JLabel(preguntas[i]);
            lbl.setForeground(Color.WHITE);
            formPanel.add(lbl, gbc);
            
            gbc.gridx = 1;
            String[] nums = new String[etiquetasOpciones[i].length];
            for(int j=0; j<nums.length; j++) nums[j] = String.valueOf(j+1);
            comboBoxes[i] = new JComboBox<>(nums);
            formPanel.add(comboBoxes[i], gbc);
        }

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(GRIS_AZULADO);
        JButton bReg = crearBoton("Registrar", new Color(0, 100, 0));
        JButton bBor = crearBoton("Borrar", VINO);
        JButton bAna = crearBoton("Analizar", AZUL_OSCURO);
        JButton bExp = crearBoton("Exportar CSV", Color.GRAY);
        
        bReg.addActionListener(e -> {
            String[] fila = new String[14];
            for (int i = 0; i < 14; i++) fila[i] = (String) comboBoxes[i].getSelectedItem();
            tableModel.addRow(fila);
        });
        bBor.addActionListener(e -> { if(table.getSelectedRow() != -1) tableModel.removeRow(table.getSelectedRow()); });
        bAna.addActionListener(e -> mostrarDashboard());
        bExp.addActionListener(e -> exportarCSV());
        
        btnPanel.add(bReg); btnPanel.add(bBor); btnPanel.add(bAna); btnPanel.add(bExp);
        gbc.gridx = 0; gbc.gridy = 16; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        tableModel = new DefaultTableModel(cabeceras, 0);
        table = new JTable(tableModel);
        table.setBackground(Color.DARK_GRAY);
        table.setForeground(Color.WHITE);
        
        TableCellRenderer headerRenderer = (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            JLabel lbl = new JLabel(value.toString());
            lbl.setOpaque(true);
            lbl.setBackground(VINO);
            lbl.setForeground(Color.WHITE);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            return lbl;
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(formPanel), new JScrollPane(table));
        splitPane.setDividerLocation(500);
        add(splitPane);
    }

    private void mostrarDashboard() {
        if(tableModel.getRowCount() == 0) return;
        JDialog d = new JDialog(this, "Análisis Detallado", true);
        d.setSize(450, 600);
        
        JComboBox<String> sel = new JComboBox<>(cabeceras);
        JPanel res = new JPanel(); res.setLayout(new BoxLayout(res, BoxLayout.Y_AXIS));
        res.setBackground(NEGRO);
        
        sel.addActionListener(e -> {
            res.removeAll();
            int col = sel.getSelectedIndex();
            Map<String, Integer> c = new HashMap<>();
            int max = 0;
            for(int i=0; i<tableModel.getRowCount(); i++) {
                String v = (String) tableModel.getValueAt(i, col);
                c.put(v, c.getOrDefault(v, 0) + 1);
                if(c.get(v) > max) max = c.get(v);
            }
            for(String opt : etiquetasOpciones[col]) {
                String num = opt.split(":")[0];
                int cant = c.getOrDefault(num, 0);
                JLabel l = new JLabel(opt + ": " + cant, SwingConstants.CENTER);
                l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                l.setOpaque(true);
                l.setBackground(cant == max && max > 0 ? AZUL_OSCURO : Color.DARK_GRAY);
                l.setForeground(Color.WHITE);
                l.setMaximumSize(new Dimension(400, 35));
                res.add(l);
            }
            res.revalidate(); res.repaint();
        });
        d.add(sel, BorderLayout.NORTH); d.add(new JScrollPane(res), BorderLayout.CENTER); d.setVisible(true);
    }

    private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");
            try (FileWriter fw = new FileWriter(f)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    fw.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) fw.append(",");
                }
                fw.append("\n");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        fw.append(tableModel.getValueAt(i, j).toString());
                        if (j < tableModel.getColumnCount() - 1) fw.append(",");
                    }
                    fw.append("\n");
                }
                JOptionPane.showMessageDialog(this, "Datos guardados como .csv correctamente.");
            } catch (IOException e) { JOptionPane.showMessageDialog(this, "Error al guardar CSV."); }
        }
    }

    private JButton crearBoton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EncuestaApp().setVisible(true));
    }
}