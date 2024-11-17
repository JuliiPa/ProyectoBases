package org.example.model;

import org.example.db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Producto {
    private int productoId;
    private String nombre;
    private double precio;
    private int stock;

    public Producto(int productoId, String nombre, double precio, int stock) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public int getProductoId() {
        return productoId;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getNombre() {
        return nombre;
    }

    public static List<Producto> obtenerTodosLosProductos() throws SQLException {
        String sql = "SELECT * FROM PB_PRODUCTO";
        List<Producto> productos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                productos.add(new Producto(
                        rs.getInt("producto_id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                ));
            }
        }
        return productos;
    }



    public static Producto obtenerProductoPorId(int productoId) throws SQLException {
        String sql = "SELECT * FROM PB_PRODUCTO WHERE producto_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Producto(
                        rs.getInt("producto_id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                );
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "Producto ID: " + productoId + ", Nombre: " + nombre + ", Precio: $" + precio + ", Stock: " + stock;
    }
}
