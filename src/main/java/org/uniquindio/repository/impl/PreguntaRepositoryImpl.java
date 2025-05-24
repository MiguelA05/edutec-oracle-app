package org.uniquindio.repository.impl;

import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.uniquindio.model.dto.PreguntaBancoDTO;
import org.uniquindio.model.entity.evaluacion.OpcionPregunta;
import org.uniquindio.model.entity.evaluacion.Pregunta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class PreguntaRepositoryImpl {

    private static final String PAQUETE_GESTION_PREGUNTAS = "PAQUETE_GESTION_PREGUNTAS";
    private static final String FUNC_BUSCAR_PREGUNTAS_BANCO_DTO = "BUSCAR_PREGUNTAS_BANCO_DTO";
    private static final String PROC_CREAR_PREGUNTA_COMPLETA = "CREAR_PREGUNTA_COMPLETA";
    private static final String PROC_ACTUALIZAR_PREGUNTA_COMPLETA = "ACTUALIZAR_PREGUNTA_COMPLETA";
    private static final String PROC_ELIMINAR_PREGUNTA_POR_ID = "ELIMINAR_PREGUNTA_POR_ID";
    private static final String FUNC_OBTENER_PREGUNTA_POR_ID_COMPLETA = "OBTENER_PREGUNTA_POR_ID_COMPLETA";
    private static final String FUNC_OBTENER_OPCIONES_POR_PREGUNTA_ID = "OBTENER_OPCIONES_POR_PREGUNTA_ID";

    // Nombres de los tipos Oracle (deben coincidir con tu definición en la BD)
    // Asegúrate que estos nombres estén en MAYÚSCULAS si así están definidos en la BD.
    private static final String T_REGISTRO_OPCION_PREGUNTA_SQL = "T_REGISTRO_OPCION_PREGUNTA";
    private static final String T_ARRAY_OPCION_PREGUNTA_SQL = "T_ARRAY_OPCION_PREGUNTA_TYPE";


    public List<PreguntaBancoDTO> buscarPreguntasBancoDTO(long cedulaProfesor, String textoBusqueda,
                                                          Integer tipoPreguntaId, Integer contenidoId, Integer nivelId) throws SQLException {
        List<PreguntaBancoDTO> preguntas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_BUSCAR_PREGUNTAS_BANCO_DTO);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setLong(2, cedulaProfesor);

            if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                cstmt.setString(3, textoBusqueda.trim());
            } else {
                cstmt.setNull(3, Types.VARCHAR);
            }

            if (tipoPreguntaId != null) cstmt.setInt(4, tipoPreguntaId);
            else cstmt.setNull(4, Types.INTEGER);

            if (contenidoId != null) cstmt.setInt(5, contenidoId);
            else cstmt.setNull(5, Types.INTEGER);

            if (nivelId != null) cstmt.setInt(6, nivelId);
            else cstmt.setNull(6, Types.INTEGER);

            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    preguntas.add(new PreguntaBancoDTO(
                            rs.getInt("ID_PREGUNTA"),
                            rs.getString("TEXTO_PREGUNTA"),
                            rs.getString("TIEMPO_ESTIMADO"),
                            rs.getBigDecimal("PORCENTAJE_DEFECTO"),
                            rs.getString("NOMBRE_TIPO_PREGUNTA"),
                            rs.getString("NOMBRE_CONTENIDO"),
                            rs.getString("NOMBRE_NIVEL"),
                            rs.getString("NOMBRE_VISIBILIDAD")
                    ));
                }
            }
        }
        return preguntas;
    }

    // En PreguntaRepositoryImpl.java

    public List<PreguntaBancoDTO> buscarPreguntasBancoDTO(long cedulaProfesor, String textoBusqueda,
                                                          Integer tipoPreguntaId, Integer contenidoId, Integer nivelId,
                                                          Integer cursoIdContexto) throws SQLException { // <--- DEBE TENER ESTE 6º PARÁMETRO
        List<PreguntaBancoDTO> preguntas = new ArrayList<>();
        // La llamada SQL debe tener 6 placeholders '?' para los parámetros IN
        String sql = String.format("{? = call %s.%s(?, ?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_BUSCAR_PREGUNTAS_BANCO_DTO);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setLong(2, cedulaProfesor); // p_cedula_profesor

            if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                cstmt.setString(3, textoBusqueda.trim()); // p_texto_busqueda
            } else {
                cstmt.setNull(3, Types.VARCHAR);
            }

            if (tipoPreguntaId != null) { // p_tipo_pregunta_id
                cstmt.setInt(4, tipoPreguntaId);
            } else {
                cstmt.setNull(4, Types.INTEGER);
            }

            if (contenidoId != null) { // p_contenido_id
                cstmt.setInt(5, contenidoId);
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }

            if (nivelId != null) { // p_nivel_id
                cstmt.setInt(6, nivelId);
            } else {
                cstmt.setNull(6, Types.INTEGER);
            }

            if (cursoIdContexto != null) { // p_id_curso_contexto
                cstmt.setInt(7, cursoIdContexto);
            } else {
                cstmt.setNull(7, Types.INTEGER);
            }

            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    preguntas.add(new PreguntaBancoDTO(
                            rs.getInt("ID_PREGUNTA"),
                            rs.getString("TEXTO_PREGUNTA"),
                            rs.getString("TIEMPO_ESTIMADO"),
                            rs.getBigDecimal("PORCENTAJE_DEFECTO"),
                            rs.getString("NOMBRE_TIPO_PREGUNTA"),
                            rs.getString("NOMBRE_CONTENIDO"),
                            rs.getString("NOMBRE_NIVEL"),
                            rs.getString("NOMBRE_VISIBILIDAD")
                    ));
                }
            }
        }
        return preguntas;
    }

    public Pregunta obtenerPreguntaPorId(int idPregunta) throws SQLException {
        Pregunta pregunta = null;
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_OBTENER_PREGUNTA_POR_ID_COMPLETA);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setInt(2, idPregunta);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                if (rs != null && rs.next()) {
                    pregunta = new Pregunta(
                            rs.getInt("ID_PREGUNTA"),
                            rs.getString("TEXTO"),
                            rs.getObject("TIEMPO_ESTIMADO", Integer.class),
                            rs.getBigDecimal("PORCENTAJE"),
                            rs.getObject("TIPO_PREGUNTA_ID", Integer.class),
                            rs.getObject("VISIBILIDAD_ID", Integer.class),
                            rs.getObject("NIVEL_ID", Integer.class),
                            rs.getObject("PREGUNTA_PADRE", Integer.class),
                            rs.getObject("CONTENIDO_ID", Integer.class),
                            rs.getInt("CREADOR_CEDULA_PROFESOR"),
                            // Para Oracle DATE que representa solo fecha, usar LocalDate
                            // Para Oracle DATE que representa fecha y hora, o TIMESTAMP, usar LocalDateTime
                            rs.getTimestamp("FECHA_CREACION") != null ? rs.getTimestamp("FECHA_CREACION").toLocalDateTime() : null,
                            rs.getTimestamp("FECHA_MODIFICACION") != null ? rs.getTimestamp("FECHA_MODIFICACION").toLocalDateTime() : null,
                            rs.getString("USUARIO_MODIFICACION")
                    );
                    // Si necesitas cargar las opciones aquí también, puedes hacerlo,
                    // aunque tu método setPreguntaParaEdicion lo hace por separado.
                    // List<OpcionPregunta> opciones = obtenerOpcionesDePregunta(idPregunta, conn);
                    // pregunta.setOpciones(opciones); // Si tu entidad Pregunta tiene un setter para opciones
                }
            }
        }
        return pregunta;
    }

    public List<OpcionPregunta> obtenerOpcionesDePregunta(int idPregunta, Connection conn) throws SQLException {
        List<OpcionPregunta> opciones = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_OBTENER_OPCIONES_POR_PREGUNTA_ID);
        boolean newConnection = false;
        Connection localConn = conn;

        if (localConn == null) {
            localConn = ConnectionOracle.conectar();
            newConnection = true;
        }

        try (CallableStatement cstmt = localConn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setInt(2, idPregunta);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    opciones.add(new OpcionPregunta(
                            rs.getInt("ID"),
                            rs.getString("RESPUESTA"),
                            rs.getString("ES_CORRECTA").charAt(0),
                            rs.getInt("PREGUNTA_ID"),
                            rs.getObject("SECUENCIA", Integer.class) // Leer la secuencia
                    ));
                }
            }
        } finally {
            if (newConnection && localConn != null) {
                localConn.close();
            }
        }
        return opciones;
    }


    public int crearPreguntaCompleta(Pregunta pregunta, List<OpcionPregunta> opciones, long cedulaProfesor) throws SQLException {
        String sql = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, PROC_CREAR_PREGUNTA_COMPLETA);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, pregunta.getTexto());
            if (pregunta.getTiempoEstimado() != null) {
                cstmt.setInt(2, pregunta.getTiempoEstimado());
            } else {
                cstmt.setNull(2, Types.INTEGER);
            }
            cstmt.setBigDecimal(3, pregunta.getPorcentaje());
            cstmt.setObject(4, pregunta.getTipoPreguntaId(), Types.INTEGER);
            cstmt.setObject(5, pregunta.getVisibilidadId(), Types.INTEGER);
            cstmt.setObject(6, pregunta.getNivelId(), Types.INTEGER);
            cstmt.setObject(7, pregunta.getPreguntaPadre(), Types.INTEGER);
            cstmt.setObject(8, pregunta.getContenidoId(), Types.INTEGER);
            cstmt.setLong(9, cedulaProfesor);

            ArrayDescriptor arrayDesc = ArrayDescriptor.createDescriptor(T_ARRAY_OPCION_PREGUNTA_SQL.toUpperCase(), conn);
            StructDescriptor structDesc = StructDescriptor.createDescriptor(T_REGISTRO_OPCION_PREGUNTA_SQL.toUpperCase(), conn);
            STRUCT[] opcionesStructArray = null;

            if (opciones != null && !opciones.isEmpty()) {
                opcionesStructArray = new STRUCT[opciones.size()];
                for (int i = 0; i < opciones.size(); i++) {
                    OpcionPregunta op = opciones.get(i);
                    Object[] attribs = new Object[]{op.getRespuesta(), String.valueOf(op.getEsCorrecta())};
                    opcionesStructArray[i] = new STRUCT(structDesc, conn, attribs);
                }
            }
            ARRAY arraySqlOpciones = new ARRAY(arrayDesc, conn, opcionesStructArray);
            cstmt.setArray(10, arraySqlOpciones);

            cstmt.registerOutParameter(11, Types.INTEGER); // p_id_pregunta_out
            cstmt.executeUpdate();
            return cstmt.getInt(11);
        }
    }

    public boolean actualizarPreguntaCompleta(Pregunta pregunta, List<OpcionPregunta> opciones, String usuarioModificacion) throws SQLException {
        String sql = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, PROC_ACTUALIZAR_PREGUNTA_COMPLETA);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, pregunta.getIdPregunta());
            cstmt.setString(2, pregunta.getTexto());
            if (pregunta.getTiempoEstimado() != null) {
                cstmt.setInt(3, pregunta.getTiempoEstimado());
            } else {
                cstmt.setNull(3, Types.INTEGER);
            }
            cstmt.setBigDecimal(4, pregunta.getPorcentaje());
            cstmt.setObject(5, pregunta.getTipoPreguntaId(), Types.INTEGER);
            cstmt.setObject(6, pregunta.getVisibilidadId(), Types.INTEGER);
            cstmt.setObject(7, pregunta.getNivelId(), Types.INTEGER);
            cstmt.setObject(8, pregunta.getPreguntaPadre(), Types.INTEGER);
            cstmt.setObject(9, pregunta.getContenidoId(), Types.INTEGER);
            cstmt.setLong(10, pregunta.getCreadorCedulaProfesor()); // Se pasa la cédula del creador original

            ArrayDescriptor arrayDesc = ArrayDescriptor.createDescriptor(T_ARRAY_OPCION_PREGUNTA_SQL.toUpperCase(), conn);
            StructDescriptor structDesc = StructDescriptor.createDescriptor(T_REGISTRO_OPCION_PREGUNTA_SQL.toUpperCase(), conn);
            STRUCT[] opcionesStructArray = null;
            if (opciones != null && !opciones.isEmpty()) {
                opcionesStructArray = new STRUCT[opciones.size()];
                for (int i = 0; i < opciones.size(); i++) {
                    OpcionPregunta op = opciones.get(i);
                    Object[] attribs = new Object[]{op.getRespuesta(), String.valueOf(op.getEsCorrecta())};
                    opcionesStructArray[i] = new STRUCT(structDesc, conn, attribs);
                }
            }
            ARRAY arraySqlOpciones = new ARRAY(arrayDesc, conn, opcionesStructArray);
            cstmt.setArray(11, arraySqlOpciones);
            cstmt.setString(12, usuarioModificacion);

            cstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar pregunta ID " + pregunta.getIdPregunta() + ": " + e.getMessage());
            throw e;
        }
    }

    public boolean eliminarPreguntaPorId(int idPregunta) throws SQLException {
        String sql = String.format("{call %s.%s(?, ?)}", PAQUETE_GESTION_PREGUNTAS, PROC_ELIMINAR_PREGUNTA_POR_ID);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, idPregunta);
            cstmt.registerOutParameter(2, Types.INTEGER);

            cstmt.executeUpdate();
            int filasAfectadas = cstmt.getInt(2);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pregunta ID " + idPregunta + ": " + e.getMessage());
            // Verificar si el error es el personalizado -20003 o -20004
            if (e.getErrorCode() == 20003 || e.getErrorCode() == 20004 ) {
                // Ya se lanzó una excepción con mensaje claro desde PL/SQL, se puede relanzar o manejar.
                // Por ahora, solo imprimimos y devolvemos false.
                // Considera si quieres que la excepción PL/SQL se propague a la UI.
                System.err.println("Error específico de PL/SQL: " + e.getMessage());
                return false; // Opcional: podrías querer relanzar la excepción para que la UI la muestre.
            }
            // Para otros errores SQL, relanzar o devolver false.
            // throw e; // Si quieres que la UI maneje todos los errores SQL.
            return false;
        }
    }

    /**
     * Lista las preguntas que pueden ser seleccionadas como "padre".
     * Excluye la pregunta que se está editando actualmente (si se proporciona su ID).
     * Solo incluye preguntas que no sean ya subpreguntas.
     *
     * @param cedulaProfesor Cédula del profesor logueado.
     * @param idPreguntaAExcluir ID de la pregunta actual que se está editando (para no listarla como su propio padre), puede ser null si se crea una nueva.
     * @return Lista de objetos Pregunta (solo con ID y Texto, o los campos necesarios para el ComboBox).
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Pregunta> listarPreguntasCandidatasPadre(long cedulaProfesor, Integer idPreguntaAExcluir) throws SQLException {
        List<Pregunta> preguntasCandidatas = new ArrayList<>();
        String sql = "{? = call PAQUETE_GESTION_PREGUNTAS.LISTAR_PREGUNTAS_CANDIDATAS_PADRE(?, ?)}";

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setLong(2, cedulaProfesor);
            if (idPreguntaAExcluir != null) {
                cstmt.setInt(3, idPreguntaAExcluir);
            } else {
                cstmt.setNull(3, Types.INTEGER);
            }
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Pregunta p = new Pregunta();
                    p.setIdPregunta(rs.getInt("ID_PREGUNTA")); // Asume que el cursor devuelve ID_PREGUNTA
                    p.setTexto(rs.getString("TEXTO"));       // Asume que el cursor devuelve TEXTO
                    // No necesitas cargar todos los campos de la pregunta, solo los necesarios para el ComboBox.
                    preguntasCandidatas.add(p);
                }
            }
        }
        return preguntasCandidatas;
    }
}
