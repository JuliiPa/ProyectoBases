package org.example.controller;

import org.example.model.Producto;
import java.sql.SQLException;
import java.util.List;

public class ProductoController {

    public List<Producto> listarProductos() throws SQLException {
        return Producto.obtenerTodosLosProductos();
    }

    public Producto obtenerProducto(int productoId) throws SQLException {
        return Producto.obtenerProductoPorId(productoId);
    }
}
