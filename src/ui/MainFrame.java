/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;
import dominio.Tarea;
import dominio.exceptions.TareaException;
import servicio.ServicioTareas;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
/**
 *
 * @author ingri
 */
public class MainFrame extends JFrame{
     private final ServicioTareas servicio;
    private DefaultTableModel tableModel;
    private JTable tareasTable;

    public MainFrame() {
        this.servicio = new ServicioTareas();
        initComponents();
        cargarTareas();
    }
 private void initComponents() {
        setTitle("Gestor de Tareas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JLabel lblTitulo = new JLabel("Título:");
        JTextField txtTitulo = new JTextField();
        
        JLabel lblPrioridad = new JLabel("Prioridad:");
        JComboBox<String> cmbPrioridad = new JComboBox<>(new String[]{"Alta", "Media", "Baja"});
                JLabel lblFecha = new JLabel("Fecha (opcional):");
        JTextField txtFecha = new JTextField();
        txtFecha.setToolTipText("Formato: dd/MM/yyyy HH:mm");
        
        JCheckBox chkEspecial = new JCheckBox("Marcar como especial (*)");
        
        JButton btnAgregar = new JButton("Agregar Tarea");
 btnAgregar.addActionListener((ActionEvent e) -> {
    try {
        String titulo = txtTitulo.getText().trim();
        int prioridad = cmbPrioridad.getSelectedIndex() + 1;
        boolean especial = chkEspecial.isSelected();
        
        LocalDate fecha = null;
        if (!txtFecha.getText().isEmpty()) {
            // Cambiar el formateador para usar LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            fecha = LocalDate.parse(txtFecha.getText(), formatter);
        }
        
        servicio.crearTarea(titulo, prioridad, especial, fecha);
        cargarTareas();
        
    } catch (TareaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use dd/MM/yyyy", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
});
          formPanel.add(lblTitulo);
        formPanel.add(txtTitulo);
        formPanel.add(lblPrioridad);
        formPanel.add(cmbPrioridad);
        formPanel.add(lblFecha);
        formPanel.add(txtFecha);
        formPanel.add(new JLabel());
        formPanel.add(chkEspecial);
        formPanel.add(new JLabel());
        formPanel.add(btnAgregar);

        // Panel de tabla
        tableModel = new DefaultTableModel(new Object[]{"ID", "Título", "Prioridad", "Estado", "Especial", "Fecha"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
          tareasTable = new JTable(tableModel);
        tareasTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tareasTable);

        // Panel de botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAlternar = new JButton("Alternar Estado");
        btnAlternar.addActionListener(e -> {
            int selectedRow = tareasTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    servicio.alternarEstado(id);
                    cargarTareas();
                } catch (TareaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
  JOptionPane.showMessageDialog(this, "Seleccione una tarea primero", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            int selectedRow = tareasTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                try {
                    servicio.eliminarTarea(id);
                    cargarTareas();
                    JOptionPane.showMessageDialog(this, "Tarea eliminada", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (TareaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                 JOptionPane.showMessageDialog(this, "Seleccione una tarea primero", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton btnDeshacer = new JButton("Deshacer Eliminación");
        btnDeshacer.addActionListener(e -> {
            try {
                servicio.deshacerUltimaEliminacion();
                cargarTareas();
                JOptionPane.showMessageDialog(this, "Última eliminación deshecha", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (TareaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
             actionPanel.add(btnAlternar);
        actionPanel.add(btnEliminar);
        actionPanel.add(btnDeshacer);

        // Barra de estado
        JLabel statusLabel = new JLabel(" Listo");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());

        // Agregar componentes al panel principal
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
private void cargarTareas() {
    tableModel.setRowCount(0);
    
    try {
        List<Tarea> tareas = servicio.listarTareas();
        
        for (Tarea tarea : tareas) {
            Object[] row = {
                tarea.getId(),
                tarea.getTitulo(),
                getPrioridadString(tarea.getPrioridad()),
                tarea.isEstado() ? "Hecho" : "Pendiente",  // Cambiado de isCompletada() a isEstado()
                tarea.isEspecial() ? "★" : "",
                tarea.getFecha() != null ? 
                    tarea.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
            };
            tableModel.addRow(row);
        }
    } catch (TareaException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private String getPrioridadString(int prioridad) {
        return switch (prioridad) {
            case 1 -> "Alta";
            case 2 -> "Media";
            case 3 -> "Baja";
            default -> "Desconocida";
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
             MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}    

