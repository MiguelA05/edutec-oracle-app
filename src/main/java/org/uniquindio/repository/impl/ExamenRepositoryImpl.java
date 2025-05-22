package org.uniquindio.repository.impl;

import org.uniquindio.model.dto.PreguntaExamenDTO; // Para pasar detalles de preguntas al crear/actualizar examen
import org.uniquindio.model.dto.PreguntaPresentacionDTO; // Para mostrar preguntas al estudiante
import org.uniquindio.model.dto.ResultadoExamenDTO; // Para mostrar resultados al estudiante
import org.uniquindio.model.dto.DetalleRespuestaPreguntaDTO; // Para el desglose de resultados
import org.uniquindio.model.entity.evaluacion.Examen;
import org.uniquindio.model.entity.evaluacion.Pregunta; // Si es necesario para algún mapeo
import org.uniquindio.model.entity.evaluacion.PresentacionExamen; // Para registrar intentos

// Importa ConnectionOracle para la conexión
// import org.uniquindio.repository.impl.ConnectionOracle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal; // Para manejar calificaciones y porcentajes
import java.util.Collections; // Para lista vacía si es necesario

public class ExamenRepositoryImpl { // Podrías implementar una interfaz ExamenRepository

    // Nombres de ejemplo para paquetes y funciones/procedimientos PL/SQL
    // Deberás ajustarlos a tu implementación real en la base de datos.
    private static final String PAQUETE_GESTION_EXAMENES = "PAQUETE_GESTION_EXAMENES";
    private static final String PROC_CREAR_EXAMEN_COMPLETO = "CREAR_EXAMEN_COMPLETO";
    private static final String PROC_ACTUALIZAR_EXAMEN_COMPLETO = "ACTUALIZAR_EXAMEN_COMPLETO";
    private static final String FUNC_OBTENER_EXAMEN_POR_ID = "OBTENER_EXAMEN_POR_ID";
    private static final String FUNC_LISTAR_EXAMENES_PROFESOR = "LISTAR_EXAMENES_POR_PROFESOR";
    private static final String FUNC_LISTAR_EXAMENES_DISPONIBLES_EST = "LISTAR_EXAMENES_DISPONIBLES_EST";
    private static final String PROC_ELIMINAR_EXAMEN = "ELIMINAR_EXAMEN_POR_ID";
    private static final String FUNC_OBTENER_PREGUNTAS_EXAMEN_DTO = "OBTENER_PREGUNTAS_EXAMEN_DTO"; // Para editar examen

    private static final String PAQUETE_PRESENTACION_EXAMEN = "PAQUETE_PRESENTACION_EXAMEN";
    private static final String FUNC_INICIAR_PRESENTACION = "INICIAR_PRESENTACION_EXAMEN"; // Devuelve ID de PresentacionExamen
    private static final String FUNC_OBTENER_PREGUNTAS_PARA_PRESENTACION = "OBTENER_PREGUNTAS_PARA_PRESENTACION"; // Devuelve PreguntaPresentacionDTO
    private static final String PROC_REGISTRAR_RESPUESTA = "REGISTRAR_RESPUESTA_ESTUDIANTE";
    // Este procedimiento ahora se encarga de calcular y devolver los datos resumen.
    private static final String PROC_FINALIZAR_Y_CALIFICAR = "FINALIZAR_Y_CALIFICAR_EXAMEN";
    // Esta función se usará para obtener el desglose detallado de las respuestas.
    private static final String FUNC_OBTENER_RESULTADOS_DETALLADOS = "OBTENER_RESULTADOS_DETALLADOS";


    private static final String PAQUETE_ESTADISTICAS_EXAMEN = "PAQUETE_ESTADISTICAS_EXAMEN"; // Para resúmenes
    private static final String FUNC_CONTAR_PROXIMOS_EXAMENES_EST = "CONTAR_PROXIMOS_EXAMENES_EST";
    private static final String FUNC_OBTENER_ULTIMO_RESULTADO_EST = "OBTENER_ULTIMO_RESULTADO_EST"; // Podría devolver un DTO o String
    private static final String FUNC_CONTAR_EXAMENES_PROFESOR = "CONTAR_EXAMENES_POR_PROFESOR";


    /**
     * Crea un examen completo con sus detalles y preguntas asociadas.
     * Llama a un procedimiento PL/SQL.
     *
     * @param examen El objeto Examen con la información general.
     * @param preguntasParaExamen Lista de DTOs con las preguntas y sus porcentajes para este examen.
     * @param cedulaProfesor Cédula del profesor que crea el examen.
     * @param seleccionAutomatica True si las preguntas se seleccionan automáticamente por el sistema.
     * @return El ID del examen creado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public int crearExamenCompleto(Examen examen, List<PreguntaExamenDTO> preguntasParaExamen, long cedulaProfesor, boolean seleccionAutomatica) throws SQLException {
        String sql = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}", // Ajusta el número de '?' según tu PL/SQL
                PAQUETE_GESTION_EXAMENES, PROC_CREAR_EXAMEN_COMPLETO);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, examen.getDescripcion());
            cstmt.setInt(2, examen.getCursoId());
            cstmt.setInt(3, examen.getCategoriaId());
            cstmt.setDate(4, new java.sql.Date(examen.getFecha().getTime()));
            cstmt.setTimestamp(5, new java.sql.Timestamp(examen.getHora().getTime()));
            cstmt.setInt(6, examen.getTiempo());
            cstmt.setInt(7, examen.getNumeroPreguntas());
            cstmt.setBigDecimal(8, examen.getCalificacionMinAprobatoria());
            cstmt.setBigDecimal(9, examen.getPesoCurso());
            cstmt.setInt(10, examen.getCreacionId());
            cstmt.setLong(11, cedulaProfesor);
            // Para p_preguntas_detalle, si es una colección Oracle:
            // ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("T_ARRAY_PREGUNTA_EXAMEN_TYPE", conn);
            // ARRAY array_of_details = new ARRAY(descriptor, conn, preguntasParaExamen.toArray()); // Necesitarías convertir DTO a un tipo compatible
            // cstmt.setArray(12, array_of_details);
            // Si no, este parámetro podría no existir y la lógica de detalle se maneja después.
            // Por ahora, asumimos que el PL/SQL lo maneja o se llama a otro procedimiento.
            // Si p_preguntas_detalle no se pasa así, ajusta el número de '?' y los índices.

            cstmt.registerOutParameter(12, Types.INTEGER); // p_id_examen_out (ajusta el índice si cambiaste el anterior)

            cstmt.executeUpdate();
            int idExamenCreado = cstmt.getInt(12); // Ajusta el índice

            if (idExamenCreado > 0) {
                if (!seleccionAutomatica && preguntasParaExamen != null && !preguntasParaExamen.isEmpty()) {
                    // Llama a un procedimiento para asignar cada pregunta manualmente
                    // PROC_ASIGNAR_PREGUNTA_A_EXAMEN(idExamenCreado, dto.getIdPregunta(), dto.getPorcentaje())
                } else if (seleccionAutomatica) {
                    // Llama a un procedimiento para agregar preguntas equilibradas
                    // PROC_AGREGAR_PREGUNTAS_EQUILIBRADAS_EXAMEN(idExamenCreado, examen.getNumeroPreguntas())
                }
            }
            return idExamenCreado;
        }
    }

    public boolean actualizarExamenCompleto(Examen examen, List<PreguntaExamenDTO> preguntasParaExamen, boolean seleccionAutomatica) throws SQLException {
        System.out.println("Simulando actualización de examen: " + examen.getId());
        return true; // Placeholder
    }

    public Examen obtenerExamenConDetallesPorId(int examenId) throws SQLException {
        Examen examen = null;
        String sqlExamen = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_EXAMENES, FUNC_OBTENER_EXAMEN_POR_ID);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmtExamen = conn.prepareCall(sqlExamen)) {

            cstmtExamen.registerOutParameter(1, Types.REF_CURSOR);
            cstmtExamen.setInt(2, examenId);
            cstmtExamen.execute();

            try (ResultSet rsExamen = (ResultSet) cstmtExamen.getObject(1)) {
                if (rsExamen != null && rsExamen.next()) {
                    examen = new Examen();
                    examen.setId(rsExamen.getInt("ID"));
                    examen.setTiempo(rsExamen.getObject("TIEMPO", Integer.class));
                    examen.setNumeroPreguntas(rsExamen.getObject("NUMERO_PREGUNTAS", Integer.class));
                    examen.setFecha(rsExamen.getDate("FECHA"));
                    examen.setHora(rsExamen.getTimestamp("HORA"));
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

    public List<PreguntaExamenDTO> obtenerPreguntasDeExamenDTO(int examenId, Connection... existingConn) throws SQLException {
        List<PreguntaExamenDTO> preguntas = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_GESTION_EXAMENES, FUNC_OBTENER_PREGUNTAS_EXAMEN_DTO);
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;

        try {
            conn = (existingConn.length > 0 && existingConn[0] != null) ? existingConn[0] : ConnectionOracle.conectar();
            cstmt = conn.prepareCall(sql);
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setInt(2, examenId);
            cstmt.execute();
            rs = (ResultSet) cstmt.getObject(1);

            while (rs != null && rs.next()) {
                preguntas.add(new PreguntaExamenDTO(
                        rs.getLong("ID_PREGUNTA"),
                        rs.getString("TEXTO_PREGUNTA"),
                        rs.getString("NOMBRE_TIPO_PREGUNTA"),
                        rs.getDouble("PORCENTAJE")
                ));
            }
        } finally {
            if (rs != null) rs.close();
            if (cstmt != null) cstmt.close();
            if (existingConn.length == 0 || existingConn[0] == null) {
                if (conn != null) conn.close();
            }
        }
        return preguntas;
    }

    public List<Examen> listarExamenesPorProfesor(long cedulaProfesor) throws SQLException {
        List<Examen> examenes = new ArrayList<>();
        // TODO: Implementar llamada a FUNC_LISTAR_EXAMENES_PROFESOR
        return examenes;
    }

    public List<Examen> listarExamenesDisponiblesParaEstudiante(long cedulaEstudiante, Integer cursoId) throws SQLException {
        List<Examen> examenes = new ArrayList<>();
        // TODO: Implementar llamada a FUNC_LISTAR_EXAMENES_DISPONIBLES_EST
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
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setInt(2, presentacionExamenId);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    // Asumiendo que el PL/SQL devuelve estas columnas:
                    // ID_PREGUNTA_EXAMEN_ESTUDIANTE, TEXTO_PREGUNTA, NOMBRE_TIPO_PREGUNTA, ID_TIPO_PREGUNTA, TIEMPO_SUGERIDO
                    // Y una forma de obtener las opciones (ej. un cursor anidado o una función separada)
                    int peeId = rs.getInt("ID_PREGUNTA_EXAMEN_ESTUDIANTE");
                    String texto = rs.getString("TEXTO_PREGUNTA");
                    String tipoNombre = rs.getString("NOMBRE_TIPO_PREGUNTA");
                    Integer tipoId = rs.getObject("ID_TIPO_PREGUNTA", Integer.class);
                    String tiempoSugerido = rs.getString("TIEMPO_SUGERIDO_PREGUNTA");

                    // TODO: Obtener las opciones para esta pregunta (si tipoId lo requiere)
                    List<PreguntaPresentacionDTO.OpcionPresentacionDTO> opciones = new ArrayList<>(); // obtenerOpcionesParaPreguntaPresentacion(peeId, conn);

                    preguntas.add(new PreguntaPresentacionDTO(peeId, texto, tipoNombre, tipoId, opciones, tiempoSugerido));
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

    /**
     * Finaliza un intento de examen, obtiene la calificación y los detalles de las respuestas.
     * Llama a un procedimiento PL/SQL para obtener el resumen y a una función para los detalles.
     * @param presentacionExamenId El ID del intento de examen.
     * @return Un objeto ResultadoExamenDTO con la nota, correctas, incorrectas y el detalle de cada respuesta.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public ResultadoExamenDTO finalizarYCalificarExamen(int presentacionExamenId) throws SQLException {
        BigDecimal calificacionFinal = BigDecimal.ZERO;
        int respuestasCorrectas = 0;
        int respuestasIncorrectas = 0;
        int preguntasRespondidas = 0;
        int totalPreguntasExamen = 0;
        String mensajeFeedbackGeneral = "";
        String nombreExamen = "";
        String fechaPresentacionStr = ""; // Fecha formateada

        // Paso 1: Llamar al procedimiento PL/SQL para finalizar, calificar y obtener el resumen.
        // PL/SQL: PROC_FINALIZAR_Y_CALIFICAR(p_id_presentacion_examen IN NUMBER,
        //                                   p_calificacion_final_out OUT NUMBER, p_resp_correctas_out OUT NUMBER, ...)
        String sqlFinalizar = String.format("{call %s.%s(?, ?, ?, ?, ?, ?, ?, ?, ?)}",
                PAQUETE_PRESENTACION_EXAMEN, PROC_FINALIZAR_Y_CALIFICAR);

        try (Connection conn = ConnectionOracle.conectar(); // Se usará para ambas llamadas
             CallableStatement cstmtFinalizar = conn.prepareCall(sqlFinalizar)) {

            cstmtFinalizar.setInt(1, presentacionExamenId);
            cstmtFinalizar.registerOutParameter(2, Types.NUMERIC); // p_calificacion_final_out
            cstmtFinalizar.registerOutParameter(3, Types.INTEGER); // p_resp_correctas_out
            cstmtFinalizar.registerOutParameter(4, Types.INTEGER); // p_resp_incorrectas_out
            cstmtFinalizar.registerOutParameter(5, Types.INTEGER); // p_preguntas_respondidas_out
            cstmtFinalizar.registerOutParameter(6, Types.INTEGER); // p_total_preguntas_examen_out
            cstmtFinalizar.registerOutParameter(7, Types.VARCHAR); // p_mensaje_feedback_out
            cstmtFinalizar.registerOutParameter(8, Types.VARCHAR); // p_nombre_examen_out
            cstmtFinalizar.registerOutParameter(9, Types.VARCHAR); // p_fecha_presentacion_out

            cstmtFinalizar.execute();

            calificacionFinal = cstmtFinalizar.getBigDecimal(2);
            respuestasCorrectas = cstmtFinalizar.getInt(3);
            respuestasIncorrectas = cstmtFinalizar.getInt(4);
            preguntasRespondidas = cstmtFinalizar.getInt(5);
            totalPreguntasExamen = cstmtFinalizar.getInt(6);
            mensajeFeedbackGeneral = cstmtFinalizar.getString(7);
            nombreExamen = cstmtFinalizar.getString(8);
            fechaPresentacionStr = cstmtFinalizar.getString(9);

            // Paso 2: Obtener el detalle de las respuestas
            List<DetalleRespuestaPreguntaDTO> detallesRespuestas = obtenerDetallesRespuestas(presentacionExamenId, conn);

            return new ResultadoExamenDTO(calificacionFinal, respuestasCorrectas, respuestasIncorrectas,
                    preguntasRespondidas, totalPreguntasExamen,
                    mensajeFeedbackGeneral, nombreExamen, fechaPresentacionStr,
                    detallesRespuestas);

        } catch (SQLException e) {
            System.err.println("Error al finalizar y calificar el examen ID: " + presentacionExamenId);
            throw e; // Relanzar la excepción para que la capa superior la maneje
        }
    }

    /**
     * Método auxiliar para obtener el desglose detallado de las respuestas.
     * Llama a la función PL/SQL FUNC_OBTENER_RESULTADOS_DETALLADOS.
     */
    private List<DetalleRespuestaPreguntaDTO> obtenerDetallesRespuestas(int presentacionExamenId, Connection conn) throws SQLException {
        List<DetalleRespuestaPreguntaDTO> detalles = new ArrayList<>();
        // PL/SQL: FUNC_OBTENER_RESULTADOS_DETALLADOS(p_id_presentacion_examen IN NUMBER) RETURN SYS_REFCURSOR;
        // El cursor debe devolver: ID_PREGUNTA_EXAMEN_ESTUDIANTE, TEXTO_PREGUNTA, NOMBRE_TIPO_PREGUNTA,
        // RESPUESTA_ESTUDIANTE, OPCIONES_CORRECTAS_TEXTO (concatenado o necesitarás otra lógica),
        // OPCIONES_CORRECTAS_ID (concatenado o necesitarás otra lógica), ES_CORRECTA (0/1), FEEDBACK_ESPECIFICO
        String sqlDetalles = String.format("{? = call %s.%s(?)}", PAQUETE_PRESENTACION_EXAMEN, FUNC_OBTENER_RESULTADOS_DETALLADOS);

        try (CallableStatement cstmtDetalles = conn.prepareCall(sqlDetalles)) {
            cstmtDetalles.registerOutParameter(1, Types.REF_CURSOR);
            cstmtDetalles.setInt(2, presentacionExamenId);
            cstmtDetalles.execute();

            try (ResultSet rs = (ResultSet) cstmtDetalles.getObject(1)) {
                while (rs != null && rs.next()) {
                    // TODO: Mapear las columnas del ResultSet a DetalleRespuestaPreguntaDTO
                    // Esto es un ejemplo, ajusta según las columnas que devuelva tu PL/SQL
                    int peeId = rs.getInt("ID_PREGUNTA_EXAMEN_ESTUDIANTE");
                    String textoP = rs.getString("TEXTO_PREGUNTA");
                    String tipoP = rs.getString("NOMBRE_TIPO_PREGUNTA");
                    String respEst = rs.getString("RESPUESTA_ESTUDIANTE"); // Puede ser ID de opción o texto

                    // Para opcionesCorrectasTexto y opcionesCorrectasId, el PL/SQL podría devolverlas
                    // como una cadena separada por comas, o podrías necesitar una subconsulta/función
                    // para cada pregunta si son múltiples.
                    List<String> optCorrectasTexto = Collections.singletonList(rs.getString("OPCIONES_CORRECTAS_TEXTO")); // Simplificado
                    List<Integer> optCorrectasId = Collections.emptyList(); // Simplificado, llenar si aplica

                    boolean esCorrecta = rs.getInt("ES_CORRECTA_LA_RESPUESTA") == 1;
                    String feedback = rs.getString("FEEDBACK_ESPECIFICO_PREGUNTA");

                    detalles.add(new DetalleRespuestaPreguntaDTO(peeId, textoP, tipoP, respEst,
                            optCorrectasTexto, optCorrectasId,
                            esCorrecta, feedback));
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
            return cstmt.getString(1);
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
