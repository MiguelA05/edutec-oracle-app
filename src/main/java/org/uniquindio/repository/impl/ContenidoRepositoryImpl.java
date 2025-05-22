package org.uniquindio.repository.impl;

import org.uniquindio.model.entity.academico.*; // Tu entidad Contenido

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenidoRepositoryImpl {

    // Asumiendo que la función para listar contenidos podría estar en PAQUETE_GESTION_CURSOS
    // o en un PAQUETE_GESTION_CONTENIDOS dedicado.
    private static final String PAQUETE_CURSOS_O_CONTENIDOS = "PAQUETE_GESTION_CURSOS"; // O el que corresponda
    private static final String FUNC_LISTAR_TODOS_LOS_CONTENIDOS = "LISTAR_TODOS_LOS_CONTENIDOS"; // Para filtro general
    private static final String FUNC_LISTAR_CONTENIDOS_POR_CURSO = "LISTAR_CONTENIDOS_POR_CURSO_PARA_FILTRO"; // Del CursoRepository

    public List<Contenido> listarTodosLosContenidos() throws SQLException {
        List<Contenido> contenidos = new ArrayList<>();
        // PL/SQL: FUNCTION LISTAR_TODOS_LOS_CONTENIDOS() RETURN SYS_REFCURSOR;
        // Devuelve ID_CONTENIDO, NOMBRE_CONTENIDO, ID_UNIDAD (opcional)
        String sql = String.format("{? = call %s.%s()}", PAQUETE_CURSOS_O_CONTENIDOS, FUNC_LISTAR_TODOS_LOS_CONTENIDOS);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    // Asumiendo que Contenido tiene constructor (id, nombre, idUnidad)
                    contenidos.add(new Contenido(
                            rs.getInt("ID_CONTENIDO"),
                            rs.getString("NOMBRE_CONTENIDO"),
                            rs.getObject("ID_UNIDAD", Integer.class) // ID_UNIDAD puede ser nulo si es un tema general
                    ));
                }
            }
        }
        return contenidos;
    }

    public List<Contenido> listarContenidosPorCurso(int cursoId) throws SQLException {
        List<Contenido> contenidos = new ArrayList<>();
        // PL/SQL: FUNCTION LISTAR_CONTENIDOS_POR_CURSO_PARA_FILTRO(p_id_curso IN NUMBER) RETURN SYS_REFCURSOR;
        // Devuelve ID_CONTENIDO, NOMBRE_CONTENIDO
        String sql = String.format("{? = call %s.%s(?)}", PAQUETE_CURSOS_O_CONTENIDOS, FUNC_LISTAR_CONTENIDOS_POR_CURSO);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.setInt(2, cursoId);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    contenidos.add(new Contenido(
                            rs.getInt("ID_CONTENIDO"),
                            rs.getString("NOMBRE_CONTENIDO"),
                            rs.getInt("ID_UNIDAD") // El ID de unidad podría no ser relevante si solo se lista para filtro por curso
                    ));
                }
            }
        }
        return contenidos;
    }
}
