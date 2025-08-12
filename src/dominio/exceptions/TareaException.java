/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dominio.exceptions;

/**
 *
 * @author ingri
 */
public class TareaException extends Exception{
     public TareaException(String message) {
        super(message);
    }
    
    public TareaException(String message, Throwable cause) {
        super(message, cause);
    }
}

