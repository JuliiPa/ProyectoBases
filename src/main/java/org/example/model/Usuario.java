package org.example.model;

import org.example.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Usuario {
    private String nombreUsuario;
    private int documento;
    private int estudianteId;


    public Usuario() {}


    public Usuario(String nombreUsuario, int documento, int estudianteId) {
        this.nombreUsuario = nombreUsuario;
        this.documento = documento;
        this.estudianteId = estudianteId;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getDocumento() {
        return documento;
    }

    public static Usuario obtenerUsuario(String nombreUsuario, int documento) throws SQLException {
        String sql = "SELECT estudiante_id, nombre, documento FROM PB_ESTUDIANTE WHERE nombre = ? AND documento = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreUsuario);
            stmt.setInt(2, documento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int estudianteId = rs.getInt("estudiante_id");
                return new Usuario(nombreUsuario, documento, estudianteId);
            }
            return null;
        }
    }

    public static Usuario obtenerUsuarioPorId(int estudianteId) throws SQLException {
        String sql = "SELECT nombre, documento FROM PB_ESTUDIANTE WHERE estudiante_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, estudianteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nombreUsuario = rs.getString("nombre");
                int documento = rs.getInt("documento");
                return new Usuario(nombreUsuario, documento, estudianteId);
            }
        }
        return null;
    }

    public int getSaldoPuntos() throws SQLException {
        String sql = "SELECT saldo_puntos FROM PB_ESTUDIANTE WHERE estudiante_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.estudianteId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("saldo_puntos");
            }
        }
        return 0;
    }

}
