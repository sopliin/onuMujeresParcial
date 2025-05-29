package org.example.onu_mujeres_crud.daos;

import org.example.onu_mujeres_crud.beans.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenidoEncuestaDAO extends BaseDAO {

    //Metodo que devuelve una lista de preguntas (Devuelve una encuesta asignada por completar a un encuestador)
    public ArrayList<BancoPreguntas> obtenerPreguntasDeEncuestaAsignada(EncuestaAsignada encuestaAsignada) {
        ArrayList<BancoPreguntas> preguntasEncuesta = new ArrayList<>();
        String sql = "SELECT \n" +
                "    ROW_NUMBER() OVER (PARTITION BY pe.encuesta_id ORDER BY pe.pregunta_id) AS orden,\n" +
                "    bp.pregunta_id, \n" +
                "    bp.texto AS pregunta, \n" +
                "    bp.tipo\n" +
                "FROM onu_mujeres.preguntas_encuesta pe\n" +
                "INNER JOIN onu_mujeres.banco_preguntas bp \n" +
                "    ON pe.pregunta_id = bp.pregunta_id\n" +
                "WHERE pe.encuesta_id = ?\n" +
                "    AND EXISTS (\n" +
                "        SELECT 1\n" +
                "        FROM onu_mujeres.encuestas_asignadas ea\n" +
                "        WHERE ea.encuesta_id = pe.encuesta_id\n" +
                "            AND ea.encuestador_id = ?\n" +
                "            AND ea.estado = 'asignada'\n" +
                "    )\n" +
                "ORDER BY pe.pregunta_id;";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, encuestaAsignada.getEncuestador().getUsuarioId());
            pstmt.setInt(2, encuestaAsignada.getEncuesta().getEncuestaId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BancoPreguntas preguntaEncuesta = new BancoPreguntas();
                    preguntaEncuesta.setPreguntaId(rs.getInt("pregunta_id"));
                    preguntaEncuesta.setTexto(rs.getString("pregunta"));
                    preguntaEncuesta.setTipo(rs.getString("tipo"));
                    preguntasEncuesta.add(preguntaEncuesta);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preguntasEncuesta;
    }

    /**
     * Guarda la respuesta general de una encuesta
     * @param respuesta Objeto Respuesta con los datos principales
     * @return ID de la respuesta generada
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public int guardarRespuesta(Respuesta respuesta) throws SQLException{
        String sql = "INSERT INTO onu_mujeres.respuestas" +
                "(asignacion_id, dni_encuestado,fecha_inicio, fecha_ultima_edicion) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, respuesta.getAsignacion().getAsignacionId());
            pstmt.setString(2,respuesta.getDniEncuestado());
            pstmt.setString(3,respuesta.getFechaInicio());
            pstmt.setString(4, respuesta.getFechaEnvio());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("No se pudo obtener el ID de la respuesta");
            }
        }
    }

    /**
     * Guarda todos los detalles de las respuestas
     * @param respuestaId ID de la respuesta general
     * @param detalles Lista de objetos RespuestaDetalle
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public void guardarDetallesRespuesta(int respuestaId, List<RespuestaDetalle> detalles) throws SQLException {
        String sql = "INSERT INTO respuestas_detalle " +
                "(respuesta_id, pregunta_id, opcion_id, respuesta_texto, fecha_contestacion) " +
                "VALUES (?, ?, ?, ?, ?)";

        try ( Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (RespuestaDetalle detalle : detalles) {
                pstmt.setInt(1, respuestaId);
                pstmt.setInt(2, detalle.getPregunta().getPreguntaId());

                if (detalle.getOpcion() != null) {
                    pstmt.setInt(3, detalle.getOpcion().getOpcionId());
                } else {
                    pstmt.setNull(3, Types.INTEGER);
                }

                if (detalle.getRespuestaTexto() != null) {
                    pstmt.setString(4, detalle.getRespuestaTexto());
                } else {
                    pstmt.setNull(4, Types.VARCHAR);
                }

                pstmt.setString(5, detalle.getFechaContestacion());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    /**
     * Método completo para guardar una encuesta respondida
     * @param respuesta Objeto Respuesta con datos principales
     * @param detalles Lista de respuestas individuales
     * @return ID de la respuesta generada
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public int guardarEncuestaCompleta(Respuesta respuesta, List<RespuestaDetalle> detalles) throws SQLException {
        try (Connection conn = getConnection()) {
            try {
                conn.setAutoCommit(false);

                int respuestaId = guardarRespuesta(respuesta);
                guardarDetallesRespuesta(respuestaId, detalles);

                conn.commit();
                return respuestaId;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Obtiene las respuestas parciales de una encuesta asignada
     * @param asignacionId ID de la asignación de encuesta
     * @return Lista de respuestas detalle ya guardadas
     * @throws SQLException Si ocurre un error en la base de datos
     */
    public List<RespuestaDetalle> obtenerRespuestasParciales(int asignacionId) throws SQLException {
        String sql = "SELECT rd.*, bp.texto AS pregunta_texto, bp.tipo AS pregunta_tipo, " +
                "po.texto_opcion AS opcion_texto " +
                "FROM respuestas r " +
                "JOIN respuestas_detalle rd ON r.respuesta_id = rd.respuesta_id " +
                "JOIN banco_preguntas bp ON rd.pregunta_id = bp.pregunta_id " +
                "LEFT JOIN pregunta_opciones po ON rd.opcion_id = po.opcion_id " +
                "WHERE r.asignacion_id = ? " +
                "ORDER BY rd.detalle_id";

        List<RespuestaDetalle> respuestas = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, asignacionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RespuestaDetalle detalle = mapearRespuestaDetalle(rs);
                    respuestas.add(detalle);
                }
            }
        }

        return respuestas;
    }
    private RespuestaDetalle mapearRespuestaDetalle(ResultSet rs) throws SQLException {
        // Mapear respuesta principal (simplificada)
        Respuesta respuesta = new Respuesta();
        respuesta.setRespuestaId(rs.getInt("respuesta_id"));

        // Mapear pregunta
        BancoPreguntas pregunta = new BancoPreguntas();
        pregunta.setPreguntaId(rs.getInt("pregunta_id"));
        pregunta.setTexto(rs.getString("pregunta_texto"));
        pregunta.setTipo(rs.getString("pregunta_tipo"));

        // Mapear opción (si existe)
        PreguntaOpcion opcion = null;
        if (rs.getObject("opcion_id") != null) {
            opcion = new PreguntaOpcion();
            opcion.setOpcionId(rs.getInt("opcion_id"));
            opcion.setTextoOpcion(rs.getString("opcion_texto"));
        }

        // Construir objeto RespuestaDetalle
        RespuestaDetalle detalle = new RespuestaDetalle();
        detalle.setDetalleId(rs.getInt("detalle_id"));
        detalle.setRespuesta(respuesta);
        detalle.setPregunta(pregunta);
        detalle.setOpcion(opcion);
        detalle.setRespuestaTexto(rs.getString("respuesta_texto"));
        detalle.setFechaContestacion(rs.getString("fecha_contestacion"));

        return detalle;
    }

}