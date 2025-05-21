package org.uniquindio.repository.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionOracle {

    // URL de conexión JDBC para Oracle.
    // Formato SID: jdbc:oracle:thin:@<host>:<puerto>:<SID>
    // Formato Service Name: jdbc:oracle:thin:@<host>:<puerto>/<service_name>

    // Basado en tu captura de pantalla de SQL Developer, el Service Name es XEPDB1.
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";

    // Usuario de la base de datos.
    private static final String USUARIO = "MIGUEL";

    // Contraseña del usuario de la base de datos.
    private static final String CONTRASENA = "12345678"; // Asegúrate que esta sea la contraseña correcta para MIGUEL

    /**
     * Establece una conexión con la base de datos Oracle.
     *
     * @return un objeto Connection a la base de datos.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public static Connection conectar() throws SQLException {
        try {
            // Asegura que el driver JDBC de Oracle esté cargado.
            // Con JDBC 4.0+ esto es a menudo automático si el JAR está en el classpath,
            // pero no hace daño cargarlo explícitamente para mayor robustez.
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error Crítico: No se encontró el driver JDBC de Oracle en el classpath.");
            e.printStackTrace();
            // Relanzar como SQLException para que el método main lo capture de forma uniforme
            throw new SQLException("Driver JDBC de Oracle no encontrado. Asegúrate de que ojdbcX.jar esté en tu classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    // Método principal de ejemplo para probar la conexión
    public static void main(String[] args) {
        Connection conexion = null;
        try {
            System.out.println("Intentando conectar a: " + URL + " con usuario: " + USUARIO);
            conexion = conectar();
            if (conexion != null) {
                System.out.println("¡Conexión a la base de datos EduTec (Oracle) exitosa!");
                // Prueba simple para verificar la conexión
                try (java.sql.Statement stmt = conexion.createStatement();
                     java.sql.ResultSet rs = stmt.executeQuery("SELECT SYSDATE FROM DUAL")) {
                    if (rs.next()) {
                        System.out.println("Fecha y hora del servidor de base de datos: " + rs.getTimestamp(1));
                    }
                }
                System.out.println("La conexión parece funcionar correctamente.");
            } else {
                // Este caso es poco probable si DriverManager.getConnection falla, ya que lanzaría SQLException.
                System.out.println("Fallo al realizar la conexión a la base de datos EduTec (DriverManager.getConnection devolvió null).");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos EduTec:");
            System.err.println("URL: " + URL);
            System.err.println("Usuario: " + USUARIO);
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            if (e.getErrorCode() == 1017) { // ORA-01017: invalid username/password; logon denied
                System.err.println("Sugerencia: Verifica que el usuario '" + USUARIO + "' y la contraseña sean correctos.");
            } else if (e.getErrorCode() == 12505) { // ORA-12505
                System.err.println("Sugerencia: Verifica que el Service Name ('XEPDB1') sea correcto y que el listener lo conozca.");
            } else if (e.getMessage() != null && e.getMessage().contains("IO Error: The Network Adapter could not establish the connection")) {
                System.err.println("Sugerencia: Verifica que la base de datos Oracle esté en ejecución en 'localhost' y que el listener esté escuchando en el puerto '1521'. Revisa también el firewall.");
            }
            e.printStackTrace();
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                    System.out.println("Conexión cerrada.");
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión:");
                    e.printStackTrace();
                }
            }
        }
    }
}
