package org.uniquindio.repository.impl;

import org.uniquindio.model.entity.academico.*;
// Importa ConnectionOracle para la conexión
// import org.uniquindio.repository.impl.ConnectionOracle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoRepositoryImpl { // Podrías implementar una interfaz CursoRepository

    private static final String PAQUETE_CURSOS = "PAQUETE_GESTION_CURSOS";
    private static final String FUNC_LISTAR_CURSOS_PROFESOR = "LISTAR_CURSOS_POR_PROFESOR";
    private static final String FUNC_LISTAR_CURSOS_ESTUDIANTE = "LISTAR_CURSOS_POR_ESTUDIANTE";
    private static final String FUNC_CONTAR_CURSOS_ESTUDIANTE = "CONTAR_CURSOS_ACTIVOS_ESTUDIANTE";
    private static final String FUNC_OBTENER_UNIDADES_CURSO = "OBTENER_UNIDADES_POR_CURSO";
    private static final String FUNC_OBTENER_CONTENIDOS_UNIDAD = "OBTENER_CONTENIDOS_POR_UNIDAD";
    private static final String PROC_CREAR_CURSO = "CREAR_NUEVO_CURSO";
    private static final String PROC_ACTUALIZAR_CURSO = "ACTUALIZAR_CURSO_EXISTENTE";
    private static final String PROC_ELIMINAR_CURSO = "ELIMINAR_CURSO_POR_ID";
    private static final String PROC_INSCRIBIR_ESTUDIANTE = "INSCRIBIR_ESTUDIANTE_EN_CURSO"; // Nuevo
    private static final String FUNC_OBTENER_CURSO_POR_ID = "OBTENER_CURSO_POR_ID";


    /**
     * Lista todos los cursos asignados a un profesor específico.
     * Llama a una función PL/SQL que devuelve un SYS_REFCURSOR.
     *
     * @param cedulaProfesor La cédula del profesor.
     * @return Una lista de objetos Curso.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Curso> listarCursosPorProfesor(long cedulaProfesor) throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_CURSOS, FUNC_LISTAR_CURSOS_PROFESOR);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.REF_CURSOR); // O OracleTypes.CURSOR
            cstmt.setLong(2, cedulaProfesor);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("ID_CURSO"));
                    curso.setNombre(rs.getString("NOMBRE"));
                    curso.setDescripcion(rs.getString("DESCRIPCION"));
                    curso.setProfesorCedula(rs.getLong("PROFESOR_CEDULA"));
                    cursos.add(curso);
                }
            }
        }
        return cursos;
    }

    /**
     * Lista todos los cursos en los que un estudiante está inscrito.
     * Llama a una función PL/SQL que devuelve un SYS_REFCURSOR.
     *
     * @param cedulaEstudiante La cédula del estudiante.
     * @return Una lista de objetos Curso.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Curso> listarCursosPorEstudiante(long cedulaEstudiante) throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_CURSOS, FUNC_LISTAR_CURSOS_ESTUDIANTE);

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setLong(2, cedulaEstudiante);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("ID_CURSO"));
                    curso.setNombre(rs.getString("NOMBRE_CURSO"));
                    curso.setDescripcion(rs.getString("DESCRIPCION_CURSO"));
                    cursos.add(curso);
                }
            }
        }
        return cursos;
    }


    /**
     * Cuenta el número de cursos activos en los que un estudiante está inscrito.
     * Llama a una función PL/SQL que devuelve un NUMBER.
     *
     * @param cedulaEstudiante La cédula del estudiante.
     * @return El número de cursos activos.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public int contarCursosActivosPorEstudiante(long cedulaEstudiante) throws SQLException {
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_CURSOS, FUNC_CONTAR_CURSOS_ESTUDIANTE);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setLong(2, cedulaEstudiante);
            cstmt.execute();
            return cstmt.getInt(1);
        }
    }

    /**
     * Obtiene las unidades de un curso específico.
     * Llama a una función PL/SQL que devuelve un SYS_REFCURSOR.
     *
     * @param cursoId El ID del curso.
     * @return Una lista de objetos Unidad.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Unidad> obtenerUnidadesPorCurso(int cursoId) throws SQLException {
        List<Unidad> unidades = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_CURSOS, FUNC_OBTENER_UNIDADES_CURSO);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setInt(2, cursoId);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Unidad unidad = new Unidad();
                    unidad.setIdUnidad(rs.getInt("ID_UNIDAD"));
                    unidad.setNombre(rs.getString("NOMBRE_UNIDAD"));
                    unidad.setIdCurso(rs.getInt("ID_CURSO"));
                    unidades.add(unidad);
                }
            }
        }
        return unidades;
    }

    /**
     * Obtiene los contenidos de una unidad específica.
     * Llama a una función PL/SQL que devuelve un SYS_REFCURSOR.
     *
     * @param unidadId El ID de la unidad.
     * @return Una lista de objetos Contenido.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Contenido> obtenerContenidosPorUnidad(int unidadId) throws SQLException {
        List<Contenido> contenidos = new ArrayList<>();
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_CURSOS, FUNC_OBTENER_CONTENIDOS_UNIDAD);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setInt(2, unidadId);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    Contenido contenido = new Contenido();
                    contenido.setIdContenido(rs.getInt("ID_CONTENIDO"));
                    contenido.setNombre(rs.getString("NOMBRE_CONTENIDO"));
                    contenido.setIdUnidad(rs.getInt("ID_UNIDAD"));
                    contenidos.add(contenido);
                }
            }
        }
        return contenidos;
    }

    /**
     * Crea un nuevo curso en la base de datos.
     * Llama a un procedimiento PL/SQL.
     *
     * @param curso El objeto Curso a crear.
     * @param cedulaProfesor La cédula del profesor que crea/asigna el curso.
     * @return El ID del curso creado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public int crearCurso(Curso curso, long cedulaProfesor) throws SQLException {
        // PL/SQL: PROCEDURE CREAR_NUEVO_CURSO(p_nombre IN VARCHAR2, p_descripcion IN VARCHAR2,
        //                                   p_cedula_profesor IN NUMBER, p_id_curso_out OUT NUMBER);
        String sql = String.format("{call %s.%s(?, ?, ?, ?)}", PAQUETE_CURSOS, PROC_CREAR_CURSO);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, curso.getNombre());
            cstmt.setString(2, curso.getDescripcion());
            cstmt.setLong(3, cedulaProfesor);
            cstmt.registerOutParameter(4, Types.INTEGER);

            cstmt.executeUpdate();
            return cstmt.getInt(4);
        }
    }

    /**
     * Actualiza un curso existente en la base de datos.
     * Llama a un procedimiento PL/SQL.
     *
     * @param curso El objeto Curso con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean actualizarCurso(Curso curso) throws SQLException {
        // PL/SQL: PROCEDURE ACTUALIZAR_CURSO_EXISTENTE(p_id_curso IN NUMBER, p_nombre IN VARCHAR2,
        //                                            p_descripcion IN VARCHAR2, p_cedula_profesor IN NUMBER);
        // O podría devolver un booleano/número para indicar éxito.
        String sql = String.format("{call %s.%s(?, ?, ?, ?)}", PAQUETE_CURSOS, PROC_ACTUALIZAR_CURSO);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, curso.getIdCurso());
            cstmt.setString(2, curso.getNombre());
            cstmt.setString(3, curso.getDescripcion());
            cstmt.setLong(4, curso.getProfesorCedula());

            int affectedRows = cstmt.executeUpdate();
            return affectedRows > 0; // O basado en un OUT parameter del PL/SQL
        }
    }

    /**
     * Elimina un curso de la base de datos por su ID.
     * Llama a un procedimiento PL/SQL.
     *
     * @param cursoId El ID del curso a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean eliminarCurso(int cursoId) throws SQLException {
        // PL/SQL: PROCEDURE ELIMINAR_CURSO_POR_ID(p_id_curso IN NUMBER);
        // El PL/SQL debería manejar la lógica de borrado en cascada o restricciones.
        String sql = String.format("{call %s.%s(?)}", PAQUETE_CURSOS, PROC_ELIMINAR_CURSO);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, cursoId);
            int affectedRows = cstmt.executeUpdate();

            return affectedRows >= 0; // executeUpdate puede devolver 0 si no hay filas afectadas pero no es error
            // o un valor específico si el PL/SQL lo indica.
            // Si el PL/SQL lanza excepción en error, este return no se alcanza.
        }
    }

    /**
     * Inscribe un estudiante en un curso.
     * Llama a un procedimiento PL/SQL.
     *
     * @param cedulaEstudiante La cédula del estudiante.
     * @param cursoId El ID del curso.
     * @return true si la inscripción fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean inscribirEstudiante(long cedulaEstudiante, int cursoId) throws SQLException {
        // PL/SQL: PROCEDURE INSCRIBIR_ESTUDIANTE_EN_CURSO(p_cedula_estudiante IN NUMBER, p_id_curso IN NUMBER);
        String sql = String.format("{call %s.%s(?, ?)}", PAQUETE_CURSOS, PROC_INSCRIBIR_ESTUDIANTE);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setLong(1, cedulaEstudiante);
            cstmt.setInt(2, cursoId);

            cstmt.executeUpdate();
            return true; // Asumir éxito si no hay excepción. El PL/SQL debería lanzar error si falla.
        }
    }

    /**
     * Obtiene un curso específico por su ID.
     * Llama a una función PL/SQL que devuelve un SYS_REFCURSOR.
     *
     * @param cursoId El ID del curso.
     * @return Un objeto Curso si se encuentra, o null si no.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Curso obtenerCursoPorId(int cursoId) throws SQLException {
        // PL/SQL: FUNCTION OBTENER_CURSO_POR_ID(p_id_curso IN NUMBER) RETURN SYS_REFCURSOR;
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_CURSOS, FUNC_OBTENER_CURSO_POR_ID);
        Curso curso = null;

        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setInt(2, cursoId);
            cstmt.execute();

            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                if (rs != null && rs.next()) {
                    curso = new Curso();
                    curso.setIdCurso(rs.getInt("ID_CURSO"));
                    curso.setNombre(rs.getString("NOMBRE"));
                    curso.setDescripcion(rs.getString("DESCRIPCION"));
                    curso.setProfesorCedula(rs.getLong("PROFESOR_CEDULA"));
                    // ... cargar otros atributos ...
                }
            }
        }
        return curso;
    }
}
