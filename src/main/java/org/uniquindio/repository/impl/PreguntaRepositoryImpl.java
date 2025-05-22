package org.uniquindio.repository.impl;

import org.uniquindio.model.dto.PreguntaBancoDTO;
import org.uniquindio.model.entity.academico.*;
import org.uniquindio.model.entity.evaluacion.OpcionPregunta;
import org.uniquindio.model.entity.evaluacion.Pregunta;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// import oracle.jdbc.OracleTypes; // Necesario si usas tipos Oracle específicos para colecciones

public class PreguntaRepositoryImpl {

    private static final String PAQUETE_GESTION_PREGUNTAS = "PAQUETE_GESTION_PREGUNTAS";
    private static final String FUNC_BUSCAR_PREGUNTAS_BANCO_DTO = "BUSCAR_PREGUNTAS_BANCO_DTO"; // Asume que devuelve DTO
    private static final String PROC_CREAR_PREGUNTA_COMPLETA = "CREAR_PREGUNTA_COMPLETA";
    private static final String PROC_ACTUALIZAR_PREGUNTA_COMPLETA = "ACTUALIZAR_PREGUNTA_COMPLETA";
    private static final String PROC_ELIMINAR_PREGUNTA_POR_ID = "ELIMINAR_PREGUNTA_POR_ID";
    private static final String FUNC_OBTENER_PREGUNTA_POR_ID = "OBTENER_PREGUNTA_POR_ID_COMPLETA"; // Para obtener entidad completa


    public List<PreguntaBancoDTO> buscarPreguntasBancoDTO(long cedulaProfesor, String textoBusqueda,
                                                          Integer tipoPreguntaId, Integer contenidoId, Integer nivelId) throws SQLException {
        List<PreguntaBancoDTO> preguntas = new ArrayList<>();
        // PL/SQL: FUNCTION BUSCAR_PREGUNTAS_BANCO_DTO(p_cedula_profesor IN NUMBER, p_texto_busqueda IN VARCHAR2,
        //                                          p_tipo_pregunta_id IN NUMBER, p_contenido_id IN NUMBER, p_nivel_id IN NUMBER)
        //                                          RETURN SYS_REFCURSOR;
        // El cursor debe devolver: ID_PREGUNTA, TEXTO, TIEMPO, PORCENTAJE_DEFECTO,
        //                         NOMBRE_TIPO_PREGUNTA, NOMBRE_CONTENIDO, NOMBRE_NIVEL, NOMBRE_VISIBILIDAD
        String sql = String.format("{? = call %s.%s(?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_BUSCAR_PREGUNTAS_BANCO_DTO);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setLong(2, cedulaProfesor);
            cstmt.setString(3, textoBusqueda); // PL/SQL manejará si es nulo o vacío para no filtrar

            if (tipoPreguntaId != null) cstmt.setInt(4, tipoPreguntaId);
            else cstmt.setNull(4, Types.INTEGER);

            if (contenidoId != null) cstmt.setInt(5, contenidoId);
            else cstmt.setNull(5, Types.INTEGER);

            if (nivelId != null) cstmt.setInt(6, nivelId); // Asumiendo que el PL/SQL tiene 6 parámetros
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

    public Pregunta obtenerPreguntaPorId(int idPregunta) throws SQLException {
        Pregunta pregunta = null;
        // PL/SQL: FUNCTION OBTENER_PREGUNTA_POR_ID_COMPLETA(p_id_pregunta IN NUMBER) RETURN SYS_REFCURSOR;
        // El cursor debe devolver todas las columnas de la tabla PREGUNTA.
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_PREGUNTAS, FUNC_OBTENER_PREGUNTA_POR_ID);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setInt(2, idPregunta);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                if (rs != null && rs.next()) {
                    pregunta = new Pregunta(
                            rs.getInt("ID_PREGUNTA"),
                            rs.getString("TEXTO"),
                            rs.getString("TIEMPO"),
                            rs.getBigDecimal("PORCENTAJE"),
                            rs.getObject("TIPO_PREGUNTA_ID", Integer.class),
                            rs.getObject("VISIBILIDAD_ID", Integer.class),
                            rs.getObject("NIVEL_ID", Integer.class),
                            rs.getObject("PREGUNTA_PADRE", Integer.class),
                            rs.getObject("CONTENIDO_ID", Integer.class)
                    );
                    // Aquí también necesitarías cargar las OpcionPregunta si la edición las requiere
                    // List<OpcionPregunta> opciones = obtenerOpcionesDePregunta(idPregunta, conn);
                    // pregunta.setOpciones(opciones); // Si tu entidad Pregunta tiene un campo para opciones
                }
            }
        }
        return pregunta;
    }


    public int crearPreguntaCompleta(Pregunta pregunta, List<OpcionPregunta> opciones, long cedulaProfesor) throws SQLException {
        // PL/SQL: PROCEDURE CREAR_PREGUNTA_COMPLETA(p_texto_pregunta IN VARCHAR2, ...,
        //                                         p_opciones IN T_ARRAY_OPCION_PREGUNTA_TYPE,
        //                                         p_id_pregunta_out OUT NUMBER);
        String sql = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}", PAQUETE_GESTION_PREGUNTAS, PROC_CREAR_PREGUNTA_COMPLETA);
        // El último ? es para p_id_pregunta_out. El parámetro de opciones es más complejo.

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, pregunta.getTexto());
            cstmt.setString(2, pregunta.getTiempo());
            cstmt.setBigDecimal(3, pregunta.getPorcentaje());
            cstmt.setObject(4, pregunta.getTipoPreguntaId(), Types.INTEGER);
            cstmt.setObject(5, pregunta.getVisibilidadId(), Types.INTEGER);
            cstmt.setObject(6, pregunta.getNivelId(), Types.INTEGER);
            cstmt.setObject(7, pregunta.getPreguntaPadre(), Types.INTEGER);
            cstmt.setObject(8, pregunta.getContenidoId(), Types.INTEGER);
            cstmt.setLong(9, cedulaProfesor);

            // Para pasar la lista de opciones, necesitas un tipo de colección Oracle
            // Object[] opcionesArray = new Object[opciones.size()];
            // for (int i = 0; i < opciones.size(); i++) {
            //     OpcionPregunta op = opciones.get(i);
            //     Object[] opcionStruct = new Object[]{op.getRespuesta(), String.valueOf(op.getEsCorrecta())};
            //     opcionesArray[i] = conn.createStruct("T_OPCION_PREGUNTA_REC", opcionStruct); // T_OPCION_PREGUNTA_REC es un OBJECT TYPE
            // }
            // ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("T_ARRAY_OPCION_PREGUNTA_TYPE", conn); // T_ARRAY_OPCION_PREGUNTA_TYPE es un TABLE OF T_OPCION_PREGUNTA_REC
            // ARRAY arraySqlOpciones = new ARRAY(descriptor, conn, opcionesArray);
            // cstmt.setArray(10, arraySqlOpciones);
            cstmt.setNull(10, Types.ARRAY); // Placeholder si no pasas la colección ahora

            cstmt.registerOutParameter(11, Types.INTEGER); // p_id_pregunta_out

            cstmt.executeUpdate();
            return cstmt.getInt(11);
        }
    }

    public boolean actualizarPreguntaCompleta(Pregunta pregunta, List<OpcionPregunta> opciones) throws SQLException {
        // Similar a crear, pero con p_id_pregunta IN y llamando a PROC_ACTUALIZAR_PREGUNTA_COMPLETA
        System.out.println("Actualización de pregunta (simulada): " + pregunta.getIdPregunta());
        return true; // Placeholder
    }

    public boolean eliminarPreguntaPorId(int idPregunta) throws SQLException {
        // PL/SQL: PROCEDURE ELIMINAR_PREGUNTA_POR_ID(p_id_pregunta IN NUMBER);
        // El PL/SQL debe manejar si se puede eliminar (ej. no usada en exámenes presentados).
        String sql = String.format("{call %s.%s(?)}", PAQUETE_GESTION_PREGUNTAS, PROC_ELIMINAR_PREGUNTA_POR_ID);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, idPregunta);
            cstmt.executeUpdate();
            // Asumir éxito si no hay excepción. El PL/SQL debería RAISERROR si no se puede.
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pregunta ID " + idPregunta + ": " + e.getMessage());
            // Podrías verificar códigos de error específicos de Oracle si tu PL/SQL los usa.
            return false;
        }
    }
}
