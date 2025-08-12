/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;
import dao.TareaDAO;
import dominio.Tarea;
import dominio.exceptions.TareaException;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
/**
 *
 * @author ingri
 */
public class ServicioTareas {
     private final TareaDAO tareaDAO;
    private final Deque<Integer> pilaEliminaciones;
    private static final int LIMITE_DESHACER = 10;

    public ServicioTareas() {
        this.tareaDAO = new TareaDAO();
        this.pilaEliminaciones = new ArrayDeque<>(LIMITE_DESHACER);
    }

    public void crearTarea(String titulo, int prioridad, boolean especial, LocalDate fecha) throws TareaException {
        Tarea tarea = new Tarea(titulo, prioridad, especial, fecha);
        tareaDAO.crearTarea(tarea);
    }

    public List<Tarea> listarTareas() throws TareaException {
        return tareaDAO.listarTareas(false);
    }

    public void alternarEstado(int id) throws TareaException {
        tareaDAO.alternarEstado(id);
    }
     public void eliminarTarea(int id) throws TareaException {
        tareaDAO.eliminarTarea(id);
        
        // Agregar a la pila para poder deshacer
        if (pilaEliminaciones.size() >= LIMITE_DESHACER) {
            pilaEliminaciones.removeFirst();
        }
        pilaEliminaciones.addLast(id);
    }

    public void deshacerUltimaEliminacion() throws TareaException {
        if (!pilaEliminaciones.isEmpty()) {
            int id = pilaEliminaciones.removeLast();
            tareaDAO.deshacerEliminacion(id);
        } else {
            throw new TareaException("No hay eliminaciones para deshacer");
        }
    }
    // Método para demostración de Streams (paradigma funcional)
    public List<Tarea> filtrarTareasPorPrioridad(int prioridad) throws TareaException {
        return tareaDAO.listarTareas(false).stream()
                .filter(t -> t.getPrioridad() == prioridad)
                .toList();
    }
}

