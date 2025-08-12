/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author ingri
 */



import dominio.Tarea;
import dominio.exceptions.TareaException;
import dominio.exceptions.ValidationException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {
    public void crearTarea(Tarea tarea) throws TareaException {
        String sql = "INSERT INTO tareas (titulo, prioridad, estado, especial, fecha) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            validarTarea(tarea);
            
            stmt.setString(1, tarea.getTitulo());
            stmt.setInt(2, tarea.getPrioridad());
            stmt.setBoolean(3, tarea.isEstado());
            stmt.setBoolean(4, tarea.isEspecial());
            stmt.setObject(5, tarea.getFecha(), Types.DATE);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new TareaException("No se pudo crear la tarea");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tarea.setId(generatedKeys.getInt(1));
                }
            }
            
        } catch (SQLException e) {
            throw new TareaException("Error al crear tarea en la base de datos", e);
        }
    }
    
    public List<Tarea> listarTareas(boolean incluirEliminadas) throws TareaException {
        String sql = "SELECT * FROM tareas WHERE eliminada = ? ORDER BY estado, prioridad, fecha_creacion DESC";
        List<Tarea> tareas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, incluirEliminadas);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Tarea tarea = resultSetToTarea(rs);
                    tareas.add(tarea);
                }
            }
            
            return tareas;
        } catch (SQLException e) {
            throw new TareaException("Error al listar tareas", e);
        }
    }
    
    public void alternarEstado(int id) throws TareaException {
        String sql = "UPDATE tareas SET estado = ~estado, fecha_modificacion = GETDATE() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new TareaException("Error al alternar estado de la tarea", e);
        }
    }
    
    public void eliminarTarea(int id) throws TareaException {
        String sql = "UPDATE tareas SET eliminada = 1, fecha_modificacion = GETDATE() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new TareaException("Error al eliminar tarea", e);
        }
    }
    
    public void deshacerEliminacion(int id) throws TareaException {
        String sql = "UPDATE tareas SET eliminada = 0, fecha_modificacion = GETDATE() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new TareaException("Error al deshacer eliminación de tarea", e);
        }
    }
    
    private Tarea resultSetToTarea(ResultSet rs) throws SQLException {
        Tarea tarea = new Tarea(
            rs.getString("titulo"),
            rs.getInt("prioridad"),
            rs.getBoolean("especial"),
            rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null
        );
        
        tarea.setId(rs.getInt("id"));
        tarea.setEstado(rs.getBoolean("estado"));
        tarea.setEliminada(rs.getBoolean("eliminada"));
        tarea.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        
        if (rs.getTimestamp("fecha_modificacion") != null) {
            tarea.setFechaModificacion(rs.getTimestamp("fecha_modificacion").toLocalDateTime());
        }
        
        return tarea;
    }
    
    private void validarTarea(Tarea tarea) throws TareaException {
        if (tarea.getTitulo() == null || tarea.getTitulo().trim().isEmpty()) {
            throw new ValidationException("El título de la tarea no puede estar vacío");
        }
        
        if (tarea.getPrioridad() < 1 || tarea.getPrioridad() > 3) {
            throw new ValidationException("La prioridad debe ser 1 (Alta), 2 (Media) o 3 (Baja)");
        }
    }
}