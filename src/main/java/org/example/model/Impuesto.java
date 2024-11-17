package org.example.model;

import org.example.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Impuesto {
    private int impuestoId;
    private String nombre;
    private double tasa;

    public Impuesto(int impuestoId, String nombre, double tasa) {
        this.impuestoId = impuestoId;
        this.nombre = nombre;
        this.tasa = tasa;
    }

    public double getTasa() {
        return tasa;
    }

    public static Impuesto obtenerImpuestoPorId(int impuestoId) throws SQLException {
        String sql = "SELECT * FROM PB_IMPUESTO WHERE impuesto_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, impuestoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Impuesto(
                        rs.getInt("impuesto_id"),
                        rs.getString("nombre"),
                        rs.getDouble("tasa")
                );
            }
            return null;
        }
    }
}
