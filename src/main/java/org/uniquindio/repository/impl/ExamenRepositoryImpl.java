package org.uniquindio.repository.impl;

import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import org.uniquindio.model.dto.DetalleRespuestaPreguntaDTO;
import org.uniquindio.model.dto.PreguntaExamenDTO;
import org.uniquindio.model.dto.PreguntaPresentacionDTO;
import org.uniquindio.model.dto.ResultadoExamenDTO;
import org.uniquindio.model.entity.evaluacion.Examen;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime; // Asegúrate que esté importado

public class ExamenRepositoryImpl {

    private static final String PAQUETE_GESTION_EXAMENES = "PAQUETE_GESTION_EXAMENES";
    private static final String PROC_CREAR_EXAMEN_COMPLETO = "CREAR_EXAMEN_COMPLETO";
    private static final String PROC_ACTUALIZAR_EXAMEN_COMPLETO = "ACTUALIZAR_EXAMEN_COMPLETO";
    private static final String FUNC_OBTENER_EXAMEN_POR_ID = "OBTENER_EXAMEN_POR_ID";
    private static final String FUNC_LISTAR_EXAMENES_PROFESOR = "LISTAR_EXAMENES_POR_PROFESOR";
    private static final String FUNC_LISTAR_EXAMENES_DISPONIBLES_EST = "LISTAR_EXAMENES_DISPONIBLES_EST";
    private static final String PROC_ELIMINAR_EXAMEN = "ELIMINAR_EXAMEN_POR_ID";
    private static final String FUNC_OBTENER_PREGUNTAS_EXAMEN_DTO = "OBTENER_PREGUNTAS_EXAMEN_DTO";

    private static final String PAQUETE_PRESENTACION_EXAMEN = "PAQUETE_PRESENTACION_EXAMEN";
    private static final String FUNC_INICIAR_PRESENTACION = "INICIAR_PRESENTACION_EXAMEN";
    private static final String FUNC_OBTENER_PREGUNTAS_PARA_PRESENTACION = "OBTENER_PREGUNTAS_PARA_PRESENTACION";
    private static final String PROC_REGISTRAR_RESPUESTA = "REGISTRAR_RESPUESTA_ESTUDIANTE";
    private static final String PROC_FINALIZAR_Y_CALIFICAR = "FINALIZAR_Y_CALIFICAR_EXAMEN";
    private static final String FUNC_OBTENER_RESULTADOS_DETALLADOS = "OBTENER_RESULTADOS_DETALLADOS";

    private static final String PAQUETE_ESTADISTICAS_EXAMEN = "PAQUETE_ESTADISTICAS_EXAMEN";
    private static final String FUNC_CONTAR_PROXIMOS_EXAMENES_EST = "CONTAR_PROXIMOS_EXAMENES_EST";
    private static final String FUNC_OBTENER_ULTIMO_RESULTADO_EST = "OBTENER_ULTIMO_RESULTADO_EST";
    private static final String FUNC_CONTAR_EXAMENES_PROFESOR = "CONTAR_EXAMENES_POR_PROFESOR";

    private static final String T_REGISTRO_PREGUNTA_EXAMEN_SQL = "T_REGISTRO_PREGUNTA_EXAMEN";
    private static final String T_ARRAY_PREGUNTA_EXAMEN_SQL = "T_ARRAY_PREGUNTA_EXAMEN_TYPE";


    public int crearExamenCompleto(Examen examen, List<PreguntaExamenDTO> preguntasParaExamen, long cedulaProfesor) throws SQLException {
        String sql = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}",
                PAQUETE_GESTION_EXAMENES, PROC_CREAR_EXAMEN_COMPLETO);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, examen.getNombre());
            cstmt.setString(2, examen.getDescripcion());
            cstmt.setInt(3, examen.getCursoId());
            cstmt.setInt(4, examen.getCategoriaId());
            cstmt.setDate(5, new java.sql.Date(examen.getFecha().getTime()));
            if (examen.getHora() != null) {
                cstmt.setTimestamp(6, Timestamp.valueOf(examen.getHora()));
            } else {
                cstmt.setNull(6, Types.TIMESTAMP);
            }
            cstmt.setInt(7, examen.getTiempo());
            cstmt.setInt(8, examen.getNumeroPreguntas());
            cstmt.setBigDecimal(9, examen.getCalificacionMinAprobatoria());
            cstmt.setBigDecimal(10, examen.getPesoCurso());
            cstmt.setInt(11, examen.getCreacionId());
            cstmt.setLong(12, cedulaProfesor);

            ArrayDescriptor arrayDesc = ArrayDescriptor.createDescriptor(T_ARRAY_PREGUNTA_EXAMEN_SQL.toUpperCase(), conn);
            StructDescriptor structDesc = StructDescriptor.createDescriptor(T_REGISTRO_PREGUNTA_EXAMEN_SQL.toUpperCase(), conn);
            STRUCT[] preguntasStructArray = null;

            if (preguntasParaExamen != null && !preguntasParaExamen.isEmpty()) {
                preguntasStructArray = new STRUCT[preguntasParaExamen.size()];
                for (int i = 0; i < preguntasParaExamen.size(); i++) {
                    PreguntaExamenDTO dto = preguntasParaExamen.get(i);
                    Object[] attribs = new Object[]{
                            BigDecimal.valueOf(dto.getIdPregunta()),
                            BigDecimal.valueOf(dto.getPorcentaje())
                    };
                    preguntasStructArray[i] = new STRUCT(structDesc, conn, attribs);
                }
            }
            ARRAY arraySqlPreguntas = new ARRAY(arrayDesc, conn, preguntasStructArray);
            cstmt.setArray(13, arraySqlPreguntas);
            cstmt.registerOutParameter(14, Types.INTEGER);
            cstmt.executeUpdate();
            return cstmt.getInt(14);
        }
    }

    public boolean actualizarExamenCompleto(Examen examen, List<PreguntaExamenDTO> preguntasParaExamen) throws SQLException {
        String sql = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}",
                PAQUETE_GESTION_EXAMENES, PROC_ACTUALIZAR_EXAMEN_COMPLETO);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, examen.getId());
            cstmt.setString(2, examen.getNombre());
            cstmt.setString(3, examen.getDescripcion());
            cstmt.setInt(4, examen.getCursoId());
            cstmt.setInt(5, examen.getCategoriaId());
            cstmt.setDate(6, new java.sql.Date(examen.getFecha().getTime()));
            if (examen.getHora() != null) {
                cstmt.setTimestamp(7, Timestamp.valueOf(examen.getHora()));
            } else {
                cstmt.setNull(7, Types.TIMESTAMP);
            }
            cstmt.setInt(8, examen.getTiempo());
            cstmt.setInt(9, examen.getNumeroPreguntas());
            cstmt.setBigDecimal(10, examen.getCalificacionMinAprobatoria());
            cstmt.setBigDecimal(11, examen.getPesoCurso());
            cstmt.setInt(12, examen.getCreacionId());

            ArrayDescriptor arrayDesc = ArrayDescriptor.createDescriptor(T_ARRAY_PREGUNTA_EXAMEN_SQL.toUpperCase(), conn);
            StructDescriptor structDesc = StructDescriptor.createDescriptor(T_REGISTRO_PREGUNTA_EXAMEN_SQL.toUpperCase(), conn);
            STRUCT[] preguntasStructArray = null;

            if (preguntasParaExamen != null && !preguntasParaExamen.isEmpty()) {
                preguntasStructArray = new STRUCT[preguntasParaExamen.size()];
                for (int i = 0; i < preguntasParaExamen.size(); i++) {
                    PreguntaExamenDTO dto = preguntasParaExamen.get(i);
                    Object[] attribs = new Object[]{
                            BigDecimal.valueOf(dto.getIdPregunta()),
                            BigDecimal.valueOf(dto.getPorcentaje())
                    };
                    preguntasStructArray[i] = new STRUCT(structDesc, conn, attribs);
                }
            }
            ARRAY arraySqlPreguntas = new ARRAY(arrayDesc, conn, preguntasStructArray);
            cstmt.setArray(13, arraySqlPreguntas);
            cstmt.executeUpdate();
            return true;
        }
    }

    public Examen obtenerExamenConDetallesPorId(int examenId) throws SQLException {
        Examen examen = null;
        String sqlExamen = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_EXAMENES, FUNC_OBTENER_EXAMEN_POR_ID);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmtExamen = conn.prepareCall(sqlExamen)) {
            cstmtExamen.registerOutParameter(1, OracleTypes.CURSOR);
            cstmtExamen.setInt(2, examenId);
            cstmtExamen.execute();
            try (ResultSet rsExamen = (ResultSet) cstmtExamen.getObject(1)) {
                if (rsExamen != null && rsExamen.next()) {
                    examen = new Examen();
                    examen.setId(rsExamen.getInt("ID"));
                    examen.setNombre(rsExamen.getString("NOMBRE"));
                    examen.setTiempo(rsExamen.getObject("TIEMPO", Integer.class));
                    examen.setNumeroPreguntas(rsExamen.getObject("NUMERO_PREGUNTAS", Integer.class));
                    examen.setFecha(rsExamen.getDate("FECHA"));
                    Timestamp horaTimestamp = rsExamen.getTimestamp("HORA");
                    if (horaTimestamp != null) {
                        examen.setHora(horaTimestamp.toLocalDateTime());
                    } else {
                        examen.setHora(null);
                    }
                    examen.setCalificacionMinAprobatoria(rsExamen.getBigDecimal("CALIFICACION_MIN_APROBATORIA"));
                    examen.setPesoCurso(rsExamen.getBigDecimal("PESO_CURSO"));
                    examen.setDescripcion(rsExamen.getString("DESCRIPCION"));
                    examen.setCreacionId(rsExamen.getObject("CREACION_ID", Integer.class));
                    examen.setCategoriaId(rsExamen.getObject("CATEGORIA_ID", Integer.class));
                    examen.setCursoId(rsExamen.getObject("CURSO_ID", Integer.class));
                }
            }
        }
        return examen;
    }

    public List<PreguntaExamenDTO> obtenerPreguntasDeExamenDTO(int examenId) throws SQLException {
        List<PreguntaExamenDTO> preguntas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_EXAMENES, FUNC_OBTENER_PREGUNTAS_EXAMEN_DTO);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setInt(2, examenId);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    preguntas.add(new PreguntaExamenDTO(
                            rs.getLong("ID_PREGUNTA"),
                            rs.getString("TEXTO_PREGUNTA"),
                            rs.getString("NOMBRE_TIPO_PREGUNTA"),
                            rs.getDouble("PORCENTAJE")
                    ));
                }
            }
        }
        return preguntas;
    }

    public List<Examen> listarExamenesPorProfesor(long cedulaProfesor) throws SQLException {
        List<Examen> examenes = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_EXAMENES, FUNC_LISTAR_EXAMENES_PROFESOR);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setLong(2, cedulaProfesor);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Examen examen = new Examen();
                    examen.setId(rs.getInt("ID_EXAMEN"));
                    examen.setNombre(rs.getString("NOMBRE_EXAMEN"));
                    examen.setDescripcion(rs.getString("DESCRIPCION_EXAMEN"));
                    // Aquí podrías cargar más detalles si la función PL/SQL los devuelve
                    examenes.add(examen);
                }
            }
        }
        return examenes;
    }

    public List<Examen> listarExamenesDisponiblesParaEstudiante(long cedulaEstudiante, Integer cursoId) throws SQLException {
        List<Examen> examenes = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?, ?)}", PAQUETE_GESTION_EXAMENES, FUNC_LISTAR_EXAMENES_DISPONIBLES_EST);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setLong(2, cedulaEstudiante);
            if (cursoId != null) {
                cstmt.setInt(3, cursoId);
            } else {
                cstmt.setNull(3, Types.INTEGER);
            }
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Examen examen = new Examen();
                    examen.setId(rs.getInt("ID"));
                    examen.setNombre(rs.getString("NOMBRE"));
                    examen.setDescripcion(rs.getString("DESCRIPCION"));
                    examen.setFecha(rs.getDate("FECHA"));
                    Timestamp horaTimestamp = rs.getTimestamp("HORA");
                    if (horaTimestamp != null) {
                        examen.setHora(horaTimestamp.toLocalDateTime());
                    } else {
                        examen.setHora(null);
                    }
                    examen.setTiempo(rs.getObject("TIEMPO", Integer.class));
                    examen.setNumeroPreguntas(rs.getObject("NUMERO_PREGUNTAS", Integer.class));
                    examen.setCursoId(rs.getObject("CURSO_ID", Integer.class));
                    // Cargar otros campos si la función PL/SQL los devuelve
                    examenes.add(examen);
                }
            }
        }
        return examenes;
    }

    public int iniciarPresentacionExamen(long cedulaEstudiante, int examenId, String ipAddress) throws SQLException {
        String sql = String.format("{? = call %s.%s(?, ?, ?)}", PAQUETE_PRESENTACION_EXAMEN, FUNC_INICIAR_PRESENTACION);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setLong(2, cedulaEstudiante);
            cstmt.setInt(3, examenId);
            cstmt.setString(4, ipAddress);
            cstmt.execute();
            return cstmt.getInt(1);
        }
    }

    public List<PreguntaPresentacionDTO> obtenerPreguntasParaPresentacion(int presentacionExamenId) throws SQLException {
        List<PreguntaPresentacionDTO> preguntas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_PRESENTACION_EXAMEN, FUNC_OBTENER_PREGUNTAS_PARA_PRESENTACION);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, OracleTypes.CURSOR);
            cstmt.setInt(2, presentacionExamenId);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    int peeId = rs.getInt("ID_PREGUNTA_EXAMEN_ESTUDIANTE");
                    int idPreguntaOriginal = rs.getInt("ID_PREGUNTA_ORIGINAL"); // Mapear nuevo campo
                    String texto = rs.getString("TEXTO_PREGUNTA");
                    String tipoNombre = rs.getString("NOMBRE_TIPO_PREGUNTA");
                    Integer tipoId = rs.getObject("TIPO_PREGUNTA_ID", Integer.class);
                    String tiempoSugerido = rs.getString("TIEMPO_SUGERIDO_PREGUNTA");
                    Integer idPreguntaPadreOriginal = rs.getObject("ID_PREGUNTA_PADRE_ORIGINAL", Integer.class);

                    List<PreguntaPresentacionDTO.OpcionPresentacionDTO> opciones = new ArrayList<>();
                    Object cursorObject = rs.getObject("OPCIONES_CURSOR");
                    if (cursorObject instanceof ResultSet) {
                        try (ResultSet rsOpciones = (ResultSet) cursorObject) {
                            while (rsOpciones.next()) {
                                opciones.add(new PreguntaPresentacionDTO.OpcionPresentacionDTO(
                                        rsOpciones.getInt("ID_OPCION"),
                                        rsOpciones.getString("TEXTO_OPCION")
                                ));
                            }
                        }
                    }
                    preguntas.add(new PreguntaPresentacionDTO(peeId, idPreguntaOriginal, texto, tipoNombre, tipoId, opciones, tiempoSugerido, idPreguntaPadreOriginal));
                }
            }
        }
        return preguntas;
    }


    public void registrarRespuestaEstudiante(int preguntaExamenEstudianteId, String respuestaDada, Integer opcionId) throws SQLException {
        String sql = String.format("{call %s.%s(?, ?, ?)}", PAQUETE_PRESENTACION_EXAMEN, PROC_REGISTRAR_RESPUESTA);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, preguntaExamenEstudianteId);
            if (respuestaDada != null) {
                cstmt.setString(2, respuestaDada);
            } else {
                cstmt.setNull(2, Types.VARCHAR);
            }
            if (opcionId != null) {
                cstmt.setInt(3, opcionId);
            } else {
                cstmt.setNull(3, Types.INTEGER);
            }
            cstmt.executeUpdate();
        }
    }

    public ResultadoExamenDTO finalizarYCalificarExamen(int presentacionExamenId) throws SQLException {
        BigDecimal calificacionFinal = BigDecimal.ZERO;
        int respuestasCorrectas = 0;
        int respuestasIncorrectas = 0;
        int preguntasRespondidas = 0;
        int totalPreguntasExamen = 0;
        String mensajeFeedbackGeneral = "";
        String nombreExamen = "";
        String fechaPresentacionStr = "";

        String sqlFinalizar = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?)}",
                PAQUETE_PRESENTACION_EXAMEN, PROC_FINALIZAR_Y_CALIFICAR);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmtFinalizar = conn.prepareCall(sqlFinalizar)) {

            cstmtFinalizar.setInt(1, presentacionExamenId);
            cstmtFinalizar.registerOutParameter(2, Types.NUMERIC);
            cstmtFinalizar.registerOutParameter(3, Types.INTEGER);
            cstmtFinalizar.registerOutParameter(4, Types.INTEGER);
            cstmtFinalizar.registerOutParameter(5, Types.INTEGER);
            cstmtFinalizar.registerOutParameter(6, Types.INTEGER);
            cstmtFinalizar.registerOutParameter(7, Types.VARCHAR);
            cstmtFinalizar.registerOutParameter(8, Types.VARCHAR);
            cstmtFinalizar.registerOutParameter(9, Types.VARCHAR);

            cstmtFinalizar.execute();

            calificacionFinal = cstmtFinalizar.getBigDecimal(2);
            respuestasCorrectas = cstmtFinalizar.getInt(3);
            respuestasIncorrectas = cstmtFinalizar.getInt(4);
            preguntasRespondidas = cstmtFinalizar.getInt(5);
            totalPreguntasExamen = cstmtFinalizar.getInt(6);
            mensajeFeedbackGeneral = cstmtFinalizar.getString(7);
            nombreExamen = cstmtFinalizar.getString(8);
            fechaPresentacionStr = cstmtFinalizar.getString(9);

            List<DetalleRespuestaPreguntaDTO> detallesRespuestas = obtenerDetallesRespuestas(presentacionExamenId, conn);

            return new ResultadoExamenDTO(calificacionFinal, respuestasCorrectas, respuestasIncorrectas,
                    preguntasRespondidas, totalPreguntasExamen,
                    mensajeFeedbackGeneral, nombreExamen, fechaPresentacionStr,
                    detallesRespuestas);

        } catch (SQLException e) {
            System.err.println("Error al finalizar y calificar el examen ID: " + presentacionExamenId);
            throw e;
        }
    }

    private List<DetalleRespuestaPreguntaDTO> obtenerDetallesRespuestas(int presentacionExamenId, Connection conn) throws SQLException {
        List<DetalleRespuestaPreguntaDTO> detalles = new ArrayList<>();
        String sqlDetalles = String.format("{? = call %s.%s(?)}", PAQUETE_PRESENTACION_EXAMEN, FUNC_OBTENER_RESULTADOS_DETALLADOS);

        try (CallableStatement cstmtDetalles = conn.prepareCall(sqlDetalles)) {
            cstmtDetalles.registerOutParameter(1, OracleTypes.CURSOR);
            cstmtDetalles.setInt(2, presentacionExamenId);
            cstmtDetalles.execute();

            try (ResultSet rs = (ResultSet) cstmtDetalles.getObject(1)) {
                while (rs != null && rs.next()) {
                    int peeId = rs.getInt("ID_PREGUNTA_EXAMEN_ESTUDIANTE");
                    String textoP = rs.getString("TEXTO_PREGUNTA");
                    String tipoP = rs.getString("NOMBRE_TIPO_PREGUNTA");
                    String respEst = rs.getString("RESPUESTA_ESTUDIANTE");
                    String opcionesCorrectasTextoStr = rs.getString("OPCIONES_CORRECTAS_TEXTO");
                    List<String> optCorrectasTexto = (opcionesCorrectasTextoStr != null && !opcionesCorrectasTextoStr.isEmpty()) ?
                            List.of(opcionesCorrectasTextoStr.split("; ")) : Collections.emptyList(); // Cambiado el split
                    String opcionesCorrectasIdStr = rs.getString("OPCIONES_CORRECTAS_ID");
                    List<Integer> optCorrectasId = new ArrayList<>();
                    if (opcionesCorrectasIdStr != null && !opcionesCorrectasIdStr.isEmpty()) {
                        for (String idStr : opcionesCorrectasIdStr.split(",")) { // Cambiado el split
                            try {
                                optCorrectasId.add(Integer.parseInt(idStr.trim()));
                            } catch (NumberFormatException e) {
                                System.err.println("Error parseando ID de opción correcta: " + idStr);
                            }
                        }
                    }
                    boolean esCorrecta = rs.getInt("ES_CORRECTA_LA_RESPUESTA") == 1;
                    String feedback = rs.getString("FEEDBACK_ESPECIFICO_PREGUNTA");
                    Integer idPreguntaPadreOriginal = rs.getObject("ID_PREGUNTA_PADRE_ORIGINAL", Integer.class); // Mapear nuevo campo

                    detalles.add(new DetalleRespuestaPreguntaDTO(peeId, textoP, tipoP, respEst,
                            optCorrectasTexto, optCorrectasId,
                            esCorrecta, feedback, idPreguntaPadreOriginal)); // Añadir al constructor
                }
            }
        }
        return detalles;
    }

    public int contarProximosExamenesEstudiante(long cedulaEstudiante) throws SQLException {
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_ESTADISTICAS_EXAMEN, FUNC_CONTAR_PROXIMOS_EXAMENES_EST);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setLong(2, cedulaEstudiante);
            cstmt.execute();
            return cstmt.getInt(1);
        }
    }

    public String obtenerDescripcionUltimoResultadoEstudiante(long cedulaEstudiante) throws SQLException {
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_ESTADISTICAS_EXAMEN, FUNC_OBTENER_ULTIMO_RESULTADO_EST);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setLong(2, cedulaEstudiante);
            cstmt.execute();
            String resultado = cstmt.getString(1);
            return resultado != null ? resultado : "";
        }
    }

    public int contarExamenesPorProfesor(long cedulaProfesor) throws SQLException {
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_ESTADISTICAS_EXAMEN, FUNC_CONTAR_EXAMENES_PROFESOR);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setLong(2, cedulaProfesor);
            cstmt.execute();
            return cstmt.getInt(1);
        }
    }
}
