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
    private static final String FUNC_LISTAR_PREGUNTAS_CANDIDATAS_PADRE = "LISTAR_PREGUNTAS_CANDIDATAS_PADRE";
    // Nueva función para obtener subpreguntas
    private static final String FUNC_OBTENER_SUBPREGUNTAS_CON_PORCENTAJE = "OBTENER_SUBPREGUNTAS_CON_PORCENTAJE";


    private static final String T_REGISTRO_OPCION_PREGUNTA_SQL = "T_REGISTRO_OPCION_PREGUNTA";
    private static final String T_ARRAY_OPCION_PREGUNTA_SQL = "T_ARRAY_OPCION_PREGUNTA_TYPE";


    public List<PreguntaBancoDTO> buscarPreguntasBancoDTO(long cedulaProfesor, String textoBusqueda,
                                                          Integer tipoPreguntaId, Integer contenidoId, Integer nivelId,
                                                          Integer cursoIdContexto) throws SQLException {
        List<PreguntaBancoDTO> preguntas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?, ?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_BUSCAR_PREGUNTAS_BANCO_DTO);

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

            if (cursoIdContexto != null) cstmt.setInt(7, cursoIdContexto);
            else cstmt.setNull(7, Types.INTEGER);


            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    PreguntaBancoDTO dto = new PreguntaBancoDTO(
                            rs.getInt("ID_PREGUNTA"),
                            rs.getString("TEXTO_PREGUNTA"),
                            rs.getString("TIEMPO_ESTIMADO"),
                            rs.getBigDecimal("PORCENTAJE_DEFECTO"),
                            rs.getString("NOMBRE_TIPO_PREGUNTA"),
                            rs.getString("NOMBRE_CONTENIDO"),
                            rs.getString("NOMBRE_NIVEL"),
                            rs.getString("NOMBRE_VISIBILIDAD")
                    );
                    // Adicionalmente, obtener el ID de la pregunta padre para el DTO
                    dto.setIdPreguntaPadre(rs.getObject("PREGUNTA_PADRE", Integer.class));
                    preguntas.add(dto);
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
                            rs.getTimestamp("FECHA_CREACION") != null ? rs.getTimestamp("FECHA_CREACION").toLocalDateTime() : null,
                            rs.getTimestamp("FECHA_MODIFICACION") != null ? rs.getTimestamp("FECHA_MODIFICACION").toLocalDateTime() : null,
                            rs.getString("USUARIO_MODIFICACION")
                    );
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
                            rs.getObject("SECUENCIA", Integer.class)
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

            cstmt.registerOutParameter(11, Types.INTEGER);
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
            cstmt.setLong(10, pregunta.getCreadorCedulaProfesor());

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
            if (e.getErrorCode() == 20003 || e.getErrorCode() == 20004 ) {
                System.err.println("Error específico de PL/SQL: " + e.getMessage());
                throw e; // Relanzar para que la UI pueda mostrar el mensaje del PL/SQL
            }
            throw e;
        }
    }

    public List<Pregunta> listarPreguntasCandidatasPadre(long cedulaProfesor, Integer idPreguntaAExcluir) throws SQLException {
        List<Pregunta> preguntasCandidatas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?, ?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_LISTAR_PREGUNTAS_CANDIDATAS_PADRE);

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
                    p.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                    p.setTexto(rs.getString("TEXTO"));
                    preguntasCandidatas.add(p);
                }
            }
        }
        return preguntasCandidatas;
    }

    /**
     * Obtiene las subpreguntas directas de una pregunta padre, incluyendo su porcentaje relativo.
     * @param idPreguntaPadre El ID de la pregunta padre.
     * @return Una lista de objetos Pregunta (subpreguntas) con su ID, texto y porcentaje.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Pregunta> obtenerSubpreguntasConPorcentaje(int idPreguntaPadre) throws SQLException {
        List<Pregunta> subpreguntas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_OBTENER_SUBPREGUNTAS_CON_PORCENTAJE);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setInt(2, idPreguntaPadre);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Pregunta subpregunta = new Pregunta();
                    subpregunta.setIdPregunta(rs.getInt("ID_PREGUNTA"));
                    subpregunta.setTexto(rs.getString("TEXTO"));
                    subpregunta.setPorcentaje(rs.getBigDecimal("PORCENTAJE"));
                    // No es necesario cargar todos los campos, solo los relevantes para esta operación.
                    subpreguntas.add(subpregunta);
                }
            }
        }
        return subpreguntas;
    }
}
