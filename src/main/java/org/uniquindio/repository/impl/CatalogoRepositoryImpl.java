package org.uniquindio.repository.impl;

import org.uniquindio.model.entity.catalogo.Nivel;
import org.uniquindio.model.entity.catalogo.TipoPregunta;
import org.uniquindio.model.entity.catalogo.Visibilidad;
import org.uniquindio.model.entity.catalogo.Categoria; // Para exámenes
import org.uniquindio.model.entity.catalogo.Creacion;   // Para exámenes


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoRepositoryImpl {

    private static final String PAQUETE_CATALOGOS = "PAQUETE_CATALOGOS";
    private static final String FUNC_LISTAR_TIPOS_PREGUNTA = "LISTAR_TIPOS_PREGUNTA";
    private static final String FUNC_LISTAR_NIVELES = "LISTAR_NIVELES";
    private static final String FUNC_LISTAR_VISIBILIDADES = "LISTAR_VISIBILIDADES";
    private static final String FUNC_LISTAR_CATEGORIAS_EXAMEN = "LISTAR_CATEGORIAS_EXAMEN";
    private static final String FUNC_LISTAR_TIPOS_CREACION_EXAMEN = "LISTAR_TIPOS_CREACION_EXAMEN";


    public List<TipoPregunta> listarTiposPregunta() throws SQLException {
        List<TipoPregunta> tipos = new ArrayList<>();
        String sql = String.format("{? = call %s.%s()}", PAQUETE_CATALOGOS, FUNC_LISTAR_TIPOS_PREGUNTA);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    // Asumiendo que TipoPregunta tiene constructor (id, nombre, descripcion)
                    // y la tabla TIPOPREGUNTA tiene columnas ID, NOMBRE, DESCRIPCION
                    tipos.add(new TipoPregunta(rs.getInt("ID"), rs.getString("NOMBRE"), rs.getString("DESCRIPCION")));
                }
            }
        }
        return tipos;
    }

    public List<Nivel> listarNiveles() throws SQLException {
        List<Nivel> niveles = new ArrayList<>();
        String sql = String.format("{? = call %s.%s()}", PAQUETE_CATALOGOS, FUNC_LISTAR_NIVELES);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    niveles.add(new Nivel(rs.getInt("ID"), rs.getString("NOMBRE"), rs.getString("DESCRIPCION")));
                }
            }
        }
        return niveles;
    }

    public List<Visibilidad> listarVisibilidades() throws SQLException {
        List<Visibilidad> visibilidades = new ArrayList<>();
        String sql = String.format("{? = call %s.%s()}", PAQUETE_CATALOGOS, FUNC_LISTAR_VISIBILIDADES);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    visibilidades.add(new Visibilidad(rs.getInt("ID"), rs.getString("NOMBRE"), rs.getString("DESCRIPCION")));
                }
            }
        }
        return visibilidades;
    }

    public List<Categoria> listarCategoriasExamen() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = String.format("{? = call %s.%s()}", PAQUETE_CATALOGOS, FUNC_LISTAR_CATEGORIAS_EXAMEN);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    // Asumiendo que Categoria tiene constructor (id, nombre, descripcion)
                    categorias.add(new Categoria(rs.getInt("ID"), rs.getString("NOMBRE"), rs.getString("DESCRIPCION")));
                }
            }
        }
        return categorias;
    }

    public List<Creacion> listarTiposCreacionExamen() throws SQLException {
        List<Creacion> creaciones = new ArrayList<>();
        String sql = String.format("{? = call %s.%s()}", PAQUETE_CATALOGOS, FUNC_LISTAR_TIPOS_CREACION_EXAMEN);
        try (Connection conn = ConnectionOracle.conectar();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.REF_CURSOR);
            cstmt.execute();
            try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                while (rs != null && rs.next()) {
                    // Asumiendo que Creacion tiene constructor (id, nombre, descripcion)
                    creaciones.add(new Creacion(rs.getInt("ID"), rs.getString("NOMBRE"), rs.getString("DESCRIPCION")));
                }
            }
        }
        return creaciones;
    }
}
