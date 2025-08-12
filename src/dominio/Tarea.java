/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio;

/**
 *
 * @author ingri
 */


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Tarea {
    private int id;
    private String titulo;
    private int prioridad; // 1=Alta, 2=Media, 3=Baja
    private boolean estado; // false=Pendiente, true=Hecho (antes "completada")
    private boolean especial;
    private LocalDate fecha; // Cambiado de LocalDateTime a LocalDate
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private boolean eliminada;

    public Tarea(String titulo, int prioridad, boolean especial, LocalDate fecha) {
        this.titulo = titulo;
        this.prioridad = prioridad;
        this.especial = especial;
        this.fecha = fecha;
        this.estado = false;
        this.eliminada = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    
    // Métodos compatibles con el nombre anterior (completada)
    public boolean isCompletada() { return estado; }
    public void setCompletada(boolean completada) { this.estado = completada; }
    
    public boolean isEspecial() { return especial; }
    public void setEspecial(boolean especial) { this.especial = especial; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { 
        this.fechaModificacion = fechaModificacion; 
    }
    
    public boolean isEliminada() { return eliminada; }
    public void setEliminada(boolean eliminada) { this.eliminada = eliminada; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaStr = fecha != null ? fecha.format(formatter) : "No especificada";
        return String.format("%d: %s (Prioridad: %d, %s) %s %s", 
            id, titulo, prioridad, 
            estado ? "Hecho" : "Pendiente",
            especial ? "★" : "",
            fechaStr);
    }
}

