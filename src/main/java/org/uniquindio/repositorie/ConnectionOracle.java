package org.uniquindio.repositorie;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionOracle {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; // Cambia el SID si no usas XE
    private static final String USUARIO = "HR";
    private static final String CONTRASENA = "HR";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
