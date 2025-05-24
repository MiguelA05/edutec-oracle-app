package org.uniquindio.repository.impl;

import oracle.jdbc.OracleTypes; // Necesario para REF_CURSOR si no usas Types.REF_CURSOR y para tipos ARRAY/STRUCT
import oracle.sql.ARRAY; // Para manejar colecciones Oracle
import oracle.sql.ArrayDescriptor; // Para describir el tipo de colección Oracle
import oracle.sql.STRUCT; // Para manejar registros Oracle
import oracle.sql.StructDescriptor; // Para describir el tipo de registro Oracle

import org.uniquindio.model.dto.PreguntaBancoDTO;
import org.uniquindio.model.entity.evaluacion.OpcionPregunta;
import org.uniquindio.model.entity.evaluacion.Pregunta; // Asegúrate que esta entidad esté actualizada

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
    private static final String T_REGISTRO_OPCION_PREGUNTA_SQL = "T_REGISTRO_OPCION_PREGUNTA";
    private static final String T_ARRAY_OPCION_PREGUNTA_SQL = "T_ARRAY_OPCION_PREGUNTA_TYPE";


    public List<PreguntaBancoDTO> buscarPreguntasBancoDTO(long cedulaProfesor, String textoBusqueda,
                                                          Integer tipoPreguntaId, Integer contenidoId, Integer nivelId) throws SQLException {
        List<PreguntaBancoDTO> preguntas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_BUSCAR_PREGUNTAS_BANCO_DTO);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR); // Usar OracleTypes.CURSOR para SYS_REFCURSOR
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
                            rs.getString("TIEMPO_ESTIMADO"), // Asumiendo que PREGUNTA.TIEMPO_ESTIMADO es NUMBER y PL/SQL lo formatea a String si es necesario, o DTO lo maneja
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

    /**
     * Obtiene una pregunta completa por su ID, incluyendo sus opciones.
     * @param idPregunta
     * @return Objeto Pregunta con sus opciones, o null si no se encuentra.
     * @throws SQLException
     */
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
                    // Asumiendo que tu entidad Pregunta.java tiene un constructor que coincide
                    // y que TIEMPO_ESTIMADO en la BD es NUMBER (lo lees como Integer).
                    // Si tu Pregunta.java espera String para tiempo, ajusta rs.getObject(...)
                    pregunta = new Pregunta(
                            rs.getInt("ID_PREGUNTA"),
                            rs.getString("TEXTO"),
                            (rs.getObject("TIEMPO_ESTIMADO", Integer.class)), // Ajustado para NUMBER
                            rs.getBigDecimal("PORCENTAJE"),
                            rs.getObject("TIPO_PREGUNTA_ID", Integer.class),
                            rs.getObject("VISIBILIDAD_ID", Integer.class),
                            rs.getObject("NIVEL_ID", Integer.class),
                            rs.getObject("PREGUNTA_PADRE", Integer.class),
                            rs.getObject("CONTENIDO_ID", Integer.class),
                            rs.getInt("CREADOR_CEDULA_PROFESOR"),
                            rs.getObject("FECHA_CREACION", java.time.LocalDateTime.class),
                            rs.getObject("FECHA_MODIFICACION", java.time.LocalDateTime.class),
                            rs.getString("USUARIO_MODIFICACION")
                    );
                    // Asignar creador si está en la entidad
                    // if (pregunta != null) {
                    //     pregunta.setCreadorCedulaProfesor(rs.getLong("CREADOR_CEDULA_PROFESOR"));
                    // }


                    // Cargar las opciones para esta pregunta
                    List<OpcionPregunta> opciones = obtenerOpcionesDePregunta(idPregunta, conn);
                    // Asumiendo que tu entidad Pregunta tiene un método setOpciones(List<OpcionPregunta> opciones)
                    // o que las pasas a un constructor más completo.
                    // pregunta.setOpciones(opciones); // Descomentar y ajustar si Pregunta tiene este setter/campo.
                }
            }
        }
        return pregunta;
    }

    /**
     * Obtiene las opciones para una pregunta específica.
     * @param idPregunta
     * @param conn Una conexión de base de datos existente (para evitar abrir múltiples conexiones).
     * @return Lista de OpcionPregunta.
     * @throws SQLException
     */
    public List<OpcionPregunta> obtenerOpcionesDePregunta(int idPregunta, Connection conn) throws SQLException {
        List<OpcionPregunta> opciones = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_OBTENER_OPCIONES_POR_PREGUNTA_ID);

        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setInt(2, idPregunta);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    opciones.add(new OpcionPregunta(
                            rs.getInt("ID"),
                            rs.getString("RESPUESTA"),
                            rs.getString("ES_CORRECTA").charAt(0), // CHAR(1)
                            rs.getInt("PREGUNTA_ID")
                    ));
                }
            }
        }
        return opciones;
    }


    public int crearPreguntaCompleta(Pregunta pregunta, List<OpcionPregunta> opciones, long cedulaProfesor) throws SQLException {
        // PL/SQL: PROCEDURE CREAR_PREGUNTA_COMPLETA(p_texto_pregunta, p_tiempo_estimado (NUMBER), p_porcentaje_defecto,
        // p_tipo_pregunta_id, p_visibilidad_id, p_nivel_id, p_pregunta_padre_id,
        // p_contenido_id, p_cedula_profesor_creador, p_opciones, p_id_pregunta_out);
        String sql = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, PROC_CREAR_PREGUNTA_COMPLETA);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, pregunta.getTexto());
            // Ajuste: TIEMPO_ESTIMADO es NUMBER en BD. La entidad Pregunta.java debe tener Integer tiempoEstimado.
            if (pregunta.getTiempoEstimado() != null) {
                cstmt.setInt(2, pregunta.getTiempoEstimado().intValue()); // Conversión explícita de Double a int
            } else {
                cstmt.setNull(2, Types.INTEGER);
            }
            cstmt.setBigDecimal(3, pregunta.getPorcentaje());
            cstmt.setObject(4, pregunta.getTipoPreguntaId(), Types.INTEGER);
            cstmt.setObject(5, pregunta.getVisibilidadId(), Types.INTEGER);
            cstmt.setObject(6, pregunta.getNivelId(), Types.INTEGER);
            cstmt.setObject(7, pregunta.getPreguntaPadre(), Types.INTEGER);
            cstmt.setObject(8, pregunta.getContenidoId(), Types.INTEGER);
            cstmt.setLong(9, cedulaProfesor); // Este es p_cedula_profesor_creador

            // Preparar la colección de opciones para PL/SQL
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
            // Incluso si opcionesStructArray es null (porque la lista de opciones estaba vacía),
            // se debe pasar un ARRAY (que puede estar vacío) o un NULL del tipo correcto si el PL/SQL lo espera.
            // Si el PL/SQL espera T_ARRAY_OPCION_PREGUNTA_TYPE y la lista es vacía, pasamos un array vacío.
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
                cstmt.setInt(3, pregunta.getTiempoEstimado().intValue());
            } else {
                cstmt.setNull(3, Types.INTEGER);
            }
            cstmt.setBigDecimal(4, pregunta.getPorcentaje());
            cstmt.setObject(5, pregunta.getTipoPreguntaId(), Types.INTEGER);
            cstmt.setObject(6, pregunta.getVisibilidadId(), Types.INTEGER);
            cstmt.setObject(7, pregunta.getNivelId(), Types.INTEGER);
            cstmt.setObject(8, pregunta.getPreguntaPadre(), Types.INTEGER);
            cstmt.setObject(9, pregunta.getContenidoId(), Types.INTEGER);
            cstmt.setLong(10, pregunta.getCreadorCedulaProfesor());

            // Preparar la colección de opciones para PL/SQL
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
            return true; // Asumir éxito si no hay excepción
        } catch (SQLException e) {
            System.err.println("Error al actualizar pregunta ID " + pregunta.getIdPregunta() + ": " + e.getMessage());
            throw e; // Relanzar para que el controlador maneje
        }
    }

    public boolean eliminarPreguntaPorId(int idPregunta) throws SQLException {
        // PL/SQL: PROCEDURE ELIMINAR_PREGUNTA_POR_ID(p_id_pregunta IN NUMBER, p_filas_afectadas_out OUT NUMBER);
        String sql = String.format("{call %s.%s(?, ?)}", PAQUETE_GESTION_PREGUNTAS, PROC_ELIMINAR_PREGUNTA_POR_ID);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, idPregunta);
            cstmt.registerOutParameter(2, Types.INTEGER); // p_filas_afectadas_out

            cstmt.executeUpdate();
            int filasAfectadas = cstmt.getInt(2);
            return filasAfectadas > 0; // El PL/SQL podría lanzar error si no se puede eliminar
        } catch (SQLException e) {
            System.err.println("Error al eliminar pregunta ID " + idPregunta + ": " + e.getMessage());
            // Podrías verificar códigos de error específicos de Oracle para "en uso".
            // Por ejemplo, si el PL/SQL lanza -20203:
            // if (e.getErrorCode() == 20203) { mostrarAlerta específica }
            return false; // O relanzar la excepción
        }
    }
}
