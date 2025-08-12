/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author ingri
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=GestorTareas;encrypt=true;trustServerCertificate=true";
    private static final String USER = "gestor_app_user";
    private static final String PASSWORD = "12345";
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties connectionProps = new Properties();
                connectionProps.put("user", USER);
                connectionProps.put("password", PASSWORD);
                connection = DriverManager.getConnection(URL, connectionProps);
                
                // Verificar y crear tabla si no existe
                initializeDatabase();
            } catch (SQLException e) {
                throw new SQLException("Error al establecer conexión con la base de datos", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        String createTableSQL = "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'tareas') "
                + "CREATE TABLE tareas ("
                + "id INT IDENTITY(1,1) NOT NULL, "
                + "titulo NVARCHAR(255) NOT NULL, "
                + "prioridad INT NOT NULL, "
                + "estado BIT NOT NULL DEFAULT 0, "
                + "especial BIT NOT NULL DEFAULT 0, "
                + "fecha DATE NULL, "
                + "fecha_creacion DATETIME2 NOT NULL DEFAULT GETDATE(), "
                + "fecha_modificacion DATETIME2 NOT NULL DEFAULT GETDATE(), "
                + "eliminada BIT NOT NULL DEFAULT 0, "
                + "CONSTRAINT PK_tareas PRIMARY KEY (id), "
                + "CONSTRAINT CHK_tareas_prioridad CHECK (prioridad BETWEEN 1 AND 3), "
                + "CONSTRAINT CHK_tareas_titulo_no_vacio CHECK (LEN(TRIM(titulo)) > 0))";
        
        try (var stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
