package org.uniquindio.repository.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class ConnectionOracle {

    // URL de conexión JDBC para Oracle.
    // Formato SID: jdbc:oracle:thin:@<host>:<puerto>:<SID>
    // Formato Service Name: jdbc:oracle:thin:@<host>:<puerto>/<service_name>

    // Basado en tu captura de pantalla de SQL Developer, el Service Name es XEPDB1.
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";

    // Usuario de la base de datos.
    private static final String USUARIO = "MIGUEL"; // Reemplaza si es necesario

    // Contraseña del usuario de la base de datos.
    private static final String CONTRASENA = "12345678"; // Reemplaza con tu contraseña real

    /**
     * Establece una conexión con la base de datos Oracle.
     *
     * @return un objeto Connection a la base de datos.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public static Connection conectar() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error Crítico: No se encontró el driver JDBC de Oracle en el classpath.");
            e.printStackTrace();
            throw new SQLException("Driver JDBC de Oracle no encontrado. Asegúrate de que ojdbcX.jar esté en tu classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    // --- Métodos de Prueba CRUD para Dia_Semana ---

    private static void probarInsertDiaSemana(Connection conexion, int id, String nombre, String descripcion) {
        System.out.println("\n--- Probando INSERT en Dia_Semana ---");
        String sql = "INSERT INTO Dia_Semana (id, nombre, descripcion) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, nombre);
            pstmt.setString(3, descripcion);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Inserción exitosa en Dia_Semana. ID: " + id + ", Nombre: " + nombre);
            } else {
                System.out.println("La inserción en Dia_Semana no afectó filas.");
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar en Dia_Semana: " + e.getMessage());
            if (e.getErrorCode() == 1) { // ORA-00001: unique constraint violated
                System.err.println("Sugerencia: El ID " + id + " ya existe en Dia_Semana. Intenta con otro ID o elimina el existente.");
            }
        }
    }

    private static void probarSelectDiaSemanaPorId(Connection conexion, int id) {
        System.out.println("\n--- Probando SELECT en Dia_Semana por ID ---");
        String sql = "SELECT id, nombre, descripcion FROM Dia_Semana WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Dia_Semana encontrado: ID=" + rs.getInt("id") +
                            ", Nombre='" + rs.getString("nombre") +
                            "', Descripcion='" + rs.getString("descripcion") + "'");
                } else {
                    System.out.println("No se encontró Dia_Semana con ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al seleccionar Dia_Semana por ID: " + e.getMessage());
        }
    }

    private static void probarUpdateDiaSemana(Connection conexion, int id, String nuevoNombre) {
        System.out.println("\n--- Probando UPDATE en Dia_Semana ---");
        String sql = "UPDATE Dia_Semana SET nombre = ? WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoNombre);
            pstmt.setInt(2, id);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Actualización exitosa en Dia_Semana para ID: " + id + ". Nuevo nombre: " + nuevoNombre);
            } else {
                System.out.println("La actualización en Dia_Semana no afectó filas (ID: " + id + " podría no existir).");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar Dia_Semana: " + e.getMessage());
            if (e.getErrorCode() == 12899) { // ORA-12899: value too large for column
                System.err.println("Sugerencia: El valor para la columna NOMBRE es demasiado largo. Máximo permitido: 20 caracteres.");
            }
        }
    }

    private static void probarSelectTodosDiasSemana(Connection conexion) {
        System.out.println("\n--- Probando SELECT de todos los Dia_Semana ---");
        String sql = "SELECT id, nombre, descripcion FROM Dia_Semana ORDER BY id";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Listado de Dias de la Semana:");
            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                System.out.println("ID: " + rs.getInt("id") +
                        ", Nombre: '" + rs.getString("nombre") +
                        "', Descripcion: '" + rs.getString("descripcion") + "'");
            }
            if (!encontrado) {
                System.out.println("No hay días de la semana registrados en la tabla.");
            }
        } catch (SQLException e) {
            System.err.println("Error al seleccionar todos los Dia_Semana: " + e.getMessage());
        }
    }

    private static void probarDeleteDiaSemana(Connection conexion, int id) {
        System.out.println("\n--- Probando DELETE en Dia_Semana ---");
        String sql = "DELETE FROM Dia_Semana WHERE id = ?";
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Eliminación exitosa en Dia_Semana para ID: " + id);
            } else {
                System.out.println("La eliminación en Dia_Semana no afectó filas (ID: " + id + " podría no existir).");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar Dia_Semana: " + e.getMessage());
        }
    }

    // --- Método de Prueba para llamar a una Función PL/SQL ---
    private static void probarLlamarFuncionListarTiposPregunta(Connection conexion) {
        System.out.println("\n--- Probando llamada a PAQUETE_CATALOGOS.LISTAR_TIPOS_PREGUNTA ---");
        // La firma en PL/SQL es: FUNCTION LISTAR_TIPOS_PREGUNTA RETURN SYS_REFCURSOR;
        String sql = "{? = call PAQUETE_CATALOGOS.LISTAR_TIPOS_PREGUNTA()}";
        try (CallableStatement cstmt = conexion.prepareCall(sql)) {
            // Registrar el parámetro de salida (el SYS_REFCURSOR)
            // En Oracle, los cursores se registran con OracleTypes.CURSOR
            cstmt.registerOutParameter(1, Types.REF_CURSOR); // Usar Types.REF_CURSOR para Oracle

            // Ejecutar la llamada
            cstmt.execute();

            // Obtener el ResultSet del cursor de salida
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                System.out.println("Tipos de Pregunta obtenidos del paquete:");
                boolean encontrado = false;
                while (rs.next()) {
                    encontrado = true;
                    // Asumiendo que la tabla TipoPregunta tiene columnas ID, NOMBRE, DESCRIPCION
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: '" + rs.getString("nombre") +
                            "', Descripcion: '" + rs.getString("descripcion") + "'");
                }
                if (!encontrado) {
                    System.out.println("No se encontraron tipos de pregunta o el paquete no devolvió resultados.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al llamar a PAQUETE_CATALOGOS.LISTAR_TIPOS_PREGUNTA: " + e.getMessage());
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("ORA-06550")) {
                System.err.println("Sugerencia: Asegúrate de que el paquete PAQUETE_CATALOGOS y su cuerpo estén compilados correctamente en la BD y que el usuario " + USUARIO + " tenga permisos de ejecución sobre él.");
            }
        }
    }


    public static void main(String[] args) {
        Connection conexion = null;
        int idPruebaDiaSemana = 99; // ID para las pruebas CRUD, asegúrate que no exista o se limpiará.
        // String nombreActualizadoPrueba = "Dia de Prueba Modificado"; // Causa ORA-12899 (24 chars)
        String nombreActualizadoPrueba = "Dia Prueba Modif"; // Corregido (16 chars <= 20)


        try {
            System.out.println("Intentando conectar a: " + URL + " con usuario: " + USUARIO);
            conexion = conectar();
            if (conexion != null) {
                System.out.println("¡Conexión a la base de datos EduTec (Oracle) exitosa!");

                // Deshabilitar AutoCommit para controlar la transacción de las pruebas
                conexion.setAutoCommit(false);
                System.out.println("AutoCommit deshabilitado para las pruebas.");

                // 1. Probar INSERT
                probarInsertDiaSemana(conexion, idPruebaDiaSemana, "Dia de Prueba", "Descripción del día de prueba");

                // 2. Probar SELECT por ID
                probarSelectDiaSemanaPorId(conexion, idPruebaDiaSemana);

                // 3. Probar UPDATE
                probarUpdateDiaSemana(conexion, idPruebaDiaSemana, nombreActualizadoPrueba);
                probarSelectDiaSemanaPorId(conexion, idPruebaDiaSemana); // Verificar actualización

                // 4. Probar SELECT de todos
                probarSelectTodosDiasSemana(conexion);

                // 5. Probar DELETE (se hará en el finally para limpiar, o aquí si se quiere probar explícitamente y luego hacer rollback)
                // Por ahora, lo dejamos para el final para que las otras pruebas puedan ver el dato.

                // 6. Probar llamada a función PL/SQL
                //probarLlamarFuncionListarTiposPregunta(conexion);

                // Si todo fue bien (o para confirmar cambios de prueba que no sean el delete final)
                // conexion.commit();
                // System.out.println("Cambios de prueba (si los hubo) confirmados con COMMIT.");
                // Para estas pruebas, es mejor hacer rollback para no dejar datos basura
                System.out.println("\nRealizando ROLLBACK para deshacer cambios de las pruebas CRUD en Dia_Semana.");
                conexion.rollback();


            } else {
                System.out.println("Fallo al realizar la conexión a la base de datos EduTec.");
            }
        } catch (SQLException e) {
            System.err.println("Error general en la ejecución de pruebas o conexión:");
            System.err.println("URL: " + URL);
            System.err.println("Usuario: " + USUARIO);
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            if (e.getErrorCode() == 1017) {
                System.err.println("Sugerencia: Verifica que el usuario '" + USUARIO + "' y la contraseña sean correctos.");
            } else if (e.getErrorCode() == 12505) {
                System.err.println("Sugerencia: Verifica que el Service Name ('XEPDB1') sea correcto y que el listener lo conozca.");
            } else if (e.getMessage() != null && e.getMessage().contains("IO Error: The Network Adapter could not establish the connection")) {
                System.err.println("Sugerencia: Verifica que la base de datos Oracle esté en ejecución en 'localhost' y que el listener esté escuchando en el puerto '1521'. Revisa también el firewall.");
            }
            e.printStackTrace();

            if(conexion != null) {
                try {
                    System.err.println("Intentando hacer ROLLBACK debido a un error...");
                    conexion.rollback();
                    System.err.println("ROLLBACK exitoso.");
                } catch (SQLException exRollback) {
                    System.err.println("Error al intentar hacer ROLLBACK: " + exRollback.getMessage());
                }
            }

        } finally {
            if (conexion != null) {
                try {
                    // Limpieza final del registro de prueba si aún existe y no se hizo rollback
                    // Para asegurar un estado limpio, intentamos eliminar el ID de prueba
                    // Esto es útil si el rollback falló o si se hizo commit en algún punto.
                    System.out.println("\n--- Limpieza Final ---");
                    // Habilitar AutoCommit para la limpieza si estaba deshabilitado y hubo rollback/commit
                    // Es importante que la limpieza se haga con autocommit o un commit explícito
                    // si la conexión principal hizo rollback.
                    boolean originalAutoCommit = conexion.getAutoCommit();
                    if (!originalAutoCommit) {
                        conexion.setAutoCommit(true);
                    }
                    probarDeleteDiaSemana(conexion, idPruebaDiaSemana);
                    if (!originalAutoCommit) { // Restaurar si lo cambiamos solo para el delete
                        // No es estrictamente necesario si vamos a cerrar, pero buena práctica.
                        // conexion.setAutoCommit(originalAutoCommit);
                    }

                    conexion.close();
                    System.out.println("\nConexión cerrada.");
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión o durante la limpieza final:");
                    e.printStackTrace();
                }
            }
        }
    }
}
