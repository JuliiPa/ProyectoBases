package org.example.model;

import org.example.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Compra {
    private int compraId;
    private int estudianteId;
    private double montoTotal = 0.0;
    private int puntosOtorgados = 0;

    private Date fechaCompra;

    // Constructor para crear una nueva compra para un estudiante
    public Compra(int estudianteId, boolean isNew) {
        this.estudianteId = estudianteId;
    }

    // Constructor para cargar una compra existente por su ID
    public Compra(int compraId) {
        this.compraId = compraId;
    }

    public Compra() {

    }

    public int getCompraId() {
        return compraId;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public int getPuntosOtorgados() {
        return puntosOtorgados;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public void crearCompra() throws SQLException {
        String sql = "INSERT INTO PB_COMPRA (estudiante_id, fecha_compra, monto_total, puntos_otorgados) " +
                "VALUES (?, SYSDATE, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"compra_id"})) {
            stmt.setInt(1, estudianteId);
            stmt.setDouble(2, montoTotal); // Inicialmente 0
            stmt.setInt(3, puntosOtorgados); // Inicialmente 0
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    this.compraId = generatedKeys.getInt(1);
                }
            }
        }
    }

    public void agregarProducto(int productoId, int cantidad) throws SQLException {
        String sql = "INSERT INTO PB_DETALLE_COMPRA (compra_id, producto_id, cantidad, subtotal) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, calcularSubtotal(productoId, cantidad));
            stmt.executeUpdate();
        }
    }

    private double calcularSubtotal(int productoId, int cantidad) throws SQLException {
        String sql = "SELECT precio FROM PB_PRODUCTO WHERE producto_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("precio") * cantidad;
            } else {
                throw new SQLException("Producto no encontrado.");
            }
        }
    }

    public double calcularTotal() throws SQLException {
        String sql = "SELECT SUM(subtotal) AS total FROM PB_DETALLE_COMPRA WHERE compra_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                montoTotal = rs.getDouble("total");
                return montoTotal;
            }
            return 0;
        }
    }

    public void procesarPago() throws SQLException {
        String sql = "UPDATE PB_COMPRA SET monto_total = ? WHERE compra_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, montoTotal);
            stmt.setInt(2, compraId);
            stmt.executeUpdate();
        }
    }

    public static Compra obtenerCompraPorId(int compraId) throws SQLException {
        String sql = "SELECT compra_id, fecha_compra, monto_total, puntos_otorgados FROM PB_COMPRA WHERE compra_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Compra compra = new Compra();
                compra.compraId = rs.getInt("compra_id");
                compra.fechaCompra = rs.getDate("fecha_compra");
                compra.montoTotal = rs.getDouble("monto_total");
                compra.puntosOtorgados = rs.getInt("puntos_otorgados");
                return compra;
            }
        }
        return null;
    }

    public static List<Compra> obtenerComprasPorFecha(int anio, int mes) throws SQLException {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT compra_id, estudiante_id, monto_total, puntos_otorgados, fecha_compra " +
                "FROM PB_COMPRA " +
                "WHERE EXTRACT(YEAR FROM fecha_compra) = ? AND EXTRACT(MONTH FROM fecha_compra) = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, anio);
            stmt.setInt(2, mes);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Compra compra = new Compra();
                compra.compraId = rs.getInt("compra_id");
                compra.estudianteId = rs.getInt("estudiante_id");
                compra.montoTotal = rs.getDouble("monto_total");
                compra.puntosOtorgados = rs.getInt("puntos_otorgados");
                compra.fechaCompra = rs.getDate("fecha_compra");
                compras.add(compra);
            }
        }
        return compras;
    }

    public double calcularMontoTotal() throws SQLException {
        List<DetalleCompra> detalles = DetalleCompra.obtenerDetallesPorCompraId(this.compraId);
        double total = 0;
        for (DetalleCompra detalle : detalles) {
            total += detalle.getSubtotal();
        }
        return total;
    }


}
