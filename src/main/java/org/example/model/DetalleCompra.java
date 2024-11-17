package org.example.model;

import org.example.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetalleCompra {
    private int detalleCompraId;
    private int compraId;
    private int productoId;
    private int cantidad;
    private double subtotal;
    private Producto producto;

    public DetalleCompra(int detalleCompraId, int compraId, int productoId, int cantidad, double subtotal) {
        this.detalleCompraId = detalleCompraId;
        this.compraId = compraId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public DetalleCompra() {

    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public int getProductoId() {
        return productoId;
    }

    public int getDetalleCompraId() {
        return detalleCompraId;
    }

    public Producto getProducto() throws SQLException {
        if (producto == null) {
            producto = Producto.obtenerProductoPorId(productoId);
        }
        return producto;
    }

    public DetalleCompra(int compraId, int productoId, int cantidad, double subtotal) {
        this.compraId = compraId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public void guardarDetalleCompra() throws SQLException {
        String sql = "INSERT INTO PB_DETALLE_COMPRA (compra_id, producto_id, cantidad, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, subtotal);
            stmt.executeUpdate();
        }
    }

    public int calcularPuntos() {
        return (int) (subtotal / 500); // 1 punto por cada $500
    }

    public static void eliminarDetallePorId(int detalleCompraId) throws SQLException {
        String sql = "DELETE FROM PB_DETALLE_COMPRA WHERE detalle_compra_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detalleCompraId);
            stmt.executeUpdate();
        }
    }

    public static void actualizarCantidad(int detalleCompraId, int nuevaCantidad, double nuevoSubtotal) throws SQLException {
        String sql = "UPDATE PB_DETALLE_COMPRA SET cantidad = ?, subtotal = ? WHERE detalle_compra_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nuevaCantidad);
            stmt.setDouble(2, nuevoSubtotal);
            stmt.setInt(3, detalleCompraId);
            stmt.executeUpdate();
        }
    }

    public static List<DetalleCompra> obtenerDetallesPorCompraId(int compraId) throws SQLException {
        List<DetalleCompra> detalles = new ArrayList<>();
        String sql = "SELECT detalle_compra_id, compra_id, producto_id, cantidad, subtotal FROM PB_DETALLE_COMPRA WHERE compra_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DetalleCompra detalle = new DetalleCompra();
                detalle.detalleCompraId = rs.getInt("detalle_compra_id");
                detalle.compraId = rs.getInt("compra_id");
                detalle.productoId = rs.getInt("producto_id");
                detalle.cantidad = rs.getInt("cantidad");
                detalle.subtotal = rs.getDouble("subtotal");
                detalles.add(detalle);
            }
        }
        return detalles;
    }
}
