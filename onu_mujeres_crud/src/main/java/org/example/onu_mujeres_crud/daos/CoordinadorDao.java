package org.example.onu_mujeres_crud.daos;
import org.example.onu_mujeres_crud.beans.*;
import java.sql.*;
import java.util.ArrayList;

public class CoordinadorDao extends BaseDAO {

    public ArrayList<Usuario> listarEncuestadores() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol_id = 2";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setUsuarioId(rs.getInt("usuario_id"));
                u.setNombre(rs.getString("nombre"));
                u.setCorreo(rs.getString("correo"));
                u.setEstado(rs.getString("estado"));
                lista.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Usuario obtenerEncuestadorPorId(int id) {
        Usuario u = null;
        String sql = "SELECT u.*, d.nombre AS nombre_distrito FROM usuarios u " +
                "LEFT JOIN distritos d ON u.distrito_id = d.distrito_id " +
                "WHERE u.usuario_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    u = new Usuario();
                    u.setUsuarioId(rs.getInt("usuario_id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setCorreo(rs.getString("correo"));
                    u.setDni(rs.getString("dni"));
                    u.setDireccion(rs.getString("direccion"));
                    u.setEstado(rs.getString("estado"));
                    Distrito distrito = new Distrito();
                    distrito.setNombre(rs.getString("nombre_distrito"));
                    usuario.setDistrito(distrito);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    public void cambiarEstadoEncuestador(int usuarioId, String nuevoEstado) {
        String sql = "UPDATE usuarios SET estado = ? WHERE usuario_id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existeDNI(String dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE dni = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    public void asignarEncuesta(String nombreEncuesta, String carpeta, int encuestadorId, int coordinadorId) {
        String sql = "INSERT INTO encuestas_asignadas (encuesta_id, encuestador_id, coordinador_id, estado) " +
                "SELECT encuesta_id, ?, ?, 'activo' FROM encuestas WHERE nombre = ? AND carpeta = ? AND estado = 'activo'";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, encuestadorId);
            stmt.setInt(2, coordinadorId);
            stmt.setString(3, nombreEncuesta);
            stmt.setString(4, carpeta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
