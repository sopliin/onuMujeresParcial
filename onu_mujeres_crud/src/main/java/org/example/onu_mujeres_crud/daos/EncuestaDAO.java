package org.example.onu_mujeres_crud.daos;

import org.example.onu_mujeres_crud.beans.Encuesta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EncuestaDAO extends BaseDAO{

    //Para obtener la lista de todas las encuestas
    public List<Encuesta> obtenerTodasLasEncuestas(int encuestadorId) {
        List<Encuesta> todasLasEncuestas = new ArrayList<>();
        String sql = "SELECT e.encuesta_id, e.nombre, e.descripcion\n" +
                "FROM encuestas_asignadas ea\n" +
                "INNER JOIN encuestas e ON ea.encuesta_id = e.encuesta_id\n" +
                "WHERE ea.encuestador_id = ? AND ea.estado = 'activo'";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, encuestadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Encuesta encuesta = new Encuesta();
                    encuesta.setEncuestaId(rs.getInt("encuesta_id"));
                    encuesta.setNombre(rs.getString("nombre"));
                    encuesta.setDescripcion(rs.getString("descripcion"));
                    todasLasEncuestas.add(encuesta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return todasLasEncuestas;
    }

    //OBtener solo las encuestas sin completas
    public List<Encuesta> obtenerFormulariosSinLlenar(int encuestadorId) {
        List<Encuesta> encuestasSinLlenar = new ArrayList<>();
        String sql = "SELECT e.encuesta_id, e.nombre, e.descripcion\n" +
                "FROM encuestas_asignadas ea\n" +
                "INNER JOIN encuestas e ON ea.encuesta_id = e.encuesta_id\n" +
                "LEFT JOIN respuestas r ON e.encuesta_id = r.encuesta_id AND r.encuestador_id = ea.encuestador_id\n" +
                "WHERE ea.encuestador_id = ? AND ea.estado = 'activo' AND r.respuesta_id IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, encuestadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Encuesta encuesta = new Encuesta();
                    encuesta.setEncuestaId(rs.getInt("encuesta_id"));
                    encuesta.setNombre(rs.getString("nombre"));
                    encuesta.setDescripcion(rs.getString("descripcion"));
                    encuestasSinLlenar.add(encuesta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return encuestasSinLlenar;
    }

    //Obtener las encuestas a medio hacer
    public List<Encuesta> obtenerFormulariosConBorradores(int encuestadorId) {
        List<Encuesta> encuestasConBorradores = new ArrayList<>();
        String sql = "SELECT e.encuesta_id, e.nombre, COUNT(r.respuesta_id) AS borradores\n" +
                "FROM encuestas_asignadas ea\n" +
                "INNER JOIN encuestas e ON ea.encuesta_id = e.encuesta_id\n" +
                "INNER JOIN respuestas r ON e.encuesta_id = r.encuesta_id AND r.encuestador_id = ea.encuestador_id\n" +
                "WHERE ea.encuestador_id = ? AND ea.estado = 'activo' AND r.estado = 'borrador'\n" +
                "GROUP BY e.encuesta_id, e.nombre";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, encuestadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Encuesta encuesta = new Encuesta();
                    encuesta.setEncuestaId(rs.getInt("encuesta_id"));
                    encuesta.setNombre(rs.getString("nombre"));
                    encuestasConBorradores.add(encuesta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return encuestasConBorradores;
    }

    //Para las encuenstas ya completadas
    public List<Encuesta> obtenerFormulariosCompletados(int encuestadorId) {
        List<Encuesta> encuestasCompletadas = new ArrayList<>();
        String sql = "SELECT e.encuesta_id, e.nombre, COUNT(r.respuesta_id) AS completadas\n" +
                "FROM encuestas_asignadas ea\n" +
                "INNER JOIN encuestas e ON ea.encuesta_id = e.encuesta_id\n" +
                "INNER JOIN respuestas r ON e.encuesta_id = r.encuesta_id AND r.encuestador_id = ea.encuestador_id\n" +
                "WHERE ea.encuestador_id = ? AND ea.estado = 'activo' AND r.estado = 'completo'\n" +
                "GROUP BY e.encuesta_id, e.nombre";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, encuestadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Encuesta encuesta = new Encuesta();
                    encuesta.setEncuestaId(rs.getInt("encuesta_id"));
                    encuesta.setNombre(rs.getString("nombre"));
                    encuestasCompletadas.add(encuesta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return encuestasCompletadas;
    }

}


