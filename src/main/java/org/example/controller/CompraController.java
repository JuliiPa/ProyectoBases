package org.example.controller;

import org.example.db.DatabaseConnection;
import org.example.model.Compra;
import org.example.model.DetalleCompra;
import org.example.model.Producto;
import org.example.model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;



public class CompraController {

    public void iniciarCompra(int estudianteId) throws SQLException {
        Compra compra = new Compra(estudianteId, true);
        compra.crearCompra();
        System.out.println("Compra iniciada con ID: " + compra.getCompraId() + " para el estudiante con ID: " + estudianteId);
    }

    public void agregarProducto(int compraId, ProductoController productoController) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Productos disponibles:");

        // Listar productos disponibles
        List<Producto> productos = productoController.listarProductos();
        for (Producto producto : productos) {
            System.out.println(producto);
        }

        // Seleccionar producto y cantidad
        System.out.print("Ingrese el ID del producto que desea agregar: ");
        int productoId = scanner.nextInt();

        System.out.print("Ingrese la cantidad del producto: ");
        int cantidad = scanner.nextInt();

        // Obtener precio y calcular subtotal
        Producto producto = productoController.obtenerProducto(productoId);
        if (producto != null) {
            double subtotal = producto.getPrecio() * cantidad;
            DetalleCompra detalle = new DetalleCompra(compraId, productoId, cantidad, subtotal);
            detalle.guardarDetalleCompra();
            System.out.println("Producto agregado a la compra con éxito.");
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    public void listarLineasDeCompra(int compraId) throws SQLException {
        List<DetalleCompra> detalles = DetalleCompra.obtenerDetallesPorCompraId(compraId);

        if (detalles.isEmpty()) {
            System.out.println("No hay líneas de compra para esta compra.");
        } else {
            System.out.println("Líneas de la Compra (Compra ID: " + compraId + "):");
            for (DetalleCompra detalle : detalles) {
                Producto producto = detalle.getProducto();
                System.out.println("Producto: " + producto.getNombre() +
                        ", Cantidad: " + detalle.getCantidad() +
                        ", Subtotal: $" + detalle.getSubtotal() +
                        ", Puntos acumulados: " + detalle.calcularPuntos());
            }
        }
    }

    public void eliminarLineaDeCompra(int compraId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Listar las líneas de la compra
        List<DetalleCompra> detalles = DetalleCompra.obtenerDetallesPorCompraId(compraId);
        if (detalles.isEmpty()) {
            System.out.println("No hay líneas de compra para esta compra.");
            return;
        }

        System.out.println("Líneas de la Compra (Compra ID: " + compraId + "):");
        for (DetalleCompra detalle : detalles) {
            System.out.println("Detalle ID: " + detalle.getDetalleCompraId() +
                    ", Producto ID: " + detalle.getProductoId() +
                    ", Cantidad: " + detalle.getCantidad() +
                    ", Subtotal: $" + detalle.getSubtotal());
        }

        // Seleccionar la línea para eliminar
        System.out.print("Ingrese el ID de la línea de compra que desea eliminar: ");
        int detalleCompraId = scanner.nextInt();

        // Eliminar la línea seleccionada
        DetalleCompra.eliminarDetallePorId(detalleCompraId);
        System.out.println("Línea de compra eliminada con éxito.");

        // Listar líneas actualizadas y totalizar la compra
        listarLineasDeCompra(compraId);
        totalizarCompra(compraId);
    }

    public void modificarCantidadDeProductoEnLinea(int compraId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Listar las líneas de la compra
        List<DetalleCompra> detalles = DetalleCompra.obtenerDetallesPorCompraId(compraId);
        if (detalles.isEmpty()) {
            System.out.println("No hay líneas de compra para esta compra.");
            return;
        }

        System.out.println("Líneas de la Compra (Compra ID: " + compraId + "):");
        for (DetalleCompra detalle : detalles) {
            System.out.println("Detalle ID: " + detalle.getDetalleCompraId() +
                    ", Producto ID: " + detalle.getProductoId() +
                    ", Cantidad: " + detalle.getCantidad() +
                    ", Subtotal: $" + detalle.getSubtotal());
        }

        // Seleccionar la línea para modificar
        System.out.print("Ingrese el ID de la línea de compra que desea modificar: ");
        int detalleCompraId = scanner.nextInt();

        // Mostrar la cantidad actual y solicitar nueva cantidad
        DetalleCompra detalleSeleccionado = null;
        for (DetalleCompra detalle : detalles) {
            if (detalle.getDetalleCompraId() == detalleCompraId) {
                detalleSeleccionado = detalle;
                break;
            }
        }

        if (detalleSeleccionado != null) {
            System.out.println("Cantidad actual: " + detalleSeleccionado.getCantidad());
            System.out.print("Ingrese la nueva cantidad: ");
            int nuevaCantidad = scanner.nextInt();

            // Obtener el producto para recalcular el subtotal
            Producto producto = Producto.obtenerProductoPorId(detalleSeleccionado.getProductoId());
            if (producto != null) {
                double nuevoSubtotal = producto.getPrecio() * nuevaCantidad;

                // Actualizar en la base de datos
                DetalleCompra.actualizarCantidad(detalleCompraId, nuevaCantidad, nuevoSubtotal);

                System.out.println("Cantidad actualizada con éxito.");

                // Listar líneas actualizadas y totalizar la compra
                listarLineasDeCompra(compraId);
                totalizarCompra(compraId);
            } else {
                System.out.println("Producto no encontrado.");
            }
        } else {
            System.out.println("Línea de compra no encontrada.");
        }
    }

    public void totalizarCompra(int compraId) throws SQLException {
        List<DetalleCompra> detalles = DetalleCompra.obtenerDetallesPorCompraId(compraId);

        if (detalles.isEmpty()) {
            System.out.println("No hay líneas de compra para esta compra.");
            return;
        }

        double total = 0;
        int totalPuntos = 0;
        for (DetalleCompra detalle : detalles) {
            total += detalle.getSubtotal();
            totalPuntos += detalle.calcularPuntos();
        }

        System.out.println("El total de la compra (ID: " + compraId + ") es: $" + total);
        System.out.println("Total de puntos acumulados en la compra: " + totalPuntos);
    }

    public void pagarCompra(int compraId) throws SQLException {
        List<DetalleCompra> detalles = DetalleCompra.obtenerDetallesPorCompraId(compraId);

        if (detalles.isEmpty()) {
            System.out.println("No hay líneas de compra para esta compra.");
            return;
        }

        // Calcular el total de la compra
        double total = 0;
        for (DetalleCompra detalle : detalles) {
            total += detalle.getSubtotal();
        }

        System.out.println("Total de la compra (ID: " + compraId + "): $" + total);

        Scanner scanner = new Scanner(System.in);
        double montoPagado = 0;

        // Solicitar la cantidad de monedas de cada denominación
        System.out.println("Ingrese la cantidad de monedas/billetes de cada denominación:");
        System.out.print("Cantidad de monedas de $1000: ");
        montoPagado += scanner.nextInt() * 1000;

        System.out.print("Cantidad de monedas de $500: ");
        montoPagado += scanner.nextInt() * 500;

        System.out.print("Cantidad de monedas de $200: ");
        montoPagado += scanner.nextInt() * 200;

        System.out.print("Cantidad de monedas de $100: ");
        montoPagado += scanner.nextInt() * 100;

        System.out.print("Cantidad de monedas de $50: ");
        montoPagado += scanner.nextInt() * 50;

        System.out.println("Monto total ingresado: $" + montoPagado);

        // Calcular el saldo o cambio
        if (montoPagado < total) {
            System.out.println("Falta por pagar: $" + (total - montoPagado));
        } else {
            System.out.println("Compra pagada exitosamente.");
            if (montoPagado > total) {
                System.out.println("Su cambio es: $" + (montoPagado - total));
            }


            int puntosOtorgados = (int) (total / 500);

            // Actualizar el monto total y puntos otorgados en la tabla PB_COMPRA
            String sqlUpdateCompra = "UPDATE PB_COMPRA SET monto_total = ?, puntos_otorgados = ? WHERE compra_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlUpdateCompra)) {
                stmt.setDouble(1, total);
                stmt.setInt(2, puntosOtorgados);
                stmt.setInt(3, compraId);
                stmt.executeUpdate();
            }

            // Obtener el estudiante_id asociado a la compra para actualizar sus puntos
            Compra compra = Compra.obtenerCompraPorId(compraId);
            int estudianteId = compra.getEstudianteId();

            // Actualizar el saldo de puntos del estudiante en la tabla PB_ESTUDIANTE
            String sqlUpdateEstudiante = "UPDATE PB_ESTUDIANTE SET saldo_puntos = saldo_puntos + ? WHERE estudiante_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlUpdateEstudiante)) {
                stmt.setInt(1, puntosOtorgados);
                stmt.setInt(2, estudianteId);
                stmt.executeUpdate();
            }

            System.out.println("Puntos otorgados: " + puntosOtorgados);
        }
    }


    public void consultarCompra(int compraId) throws SQLException {
        Compra compra = Compra.obtenerCompraPorId(compraId);

        if (compra == null) {
            System.out.println("La compra con ID " + compraId + " no existe.");
            return;
        }

        System.out.println("Compra ID: " + compraId);
        System.out.println("Fecha de compra: " + compra.getFechaCompra());
        System.out.println("Monto total: $" + compra.getMontoTotal());
        System.out.println("Puntos otorgados: " + compra.getPuntosOtorgados());
        System.out.println("Detalles de la compra:");

        List<DetalleCompra> detalles = DetalleCompra.obtenerDetallesPorCompraId(compraId);

        for (DetalleCompra detalle : detalles) {
            Producto producto = Producto.obtenerProductoPorId(detalle.getProductoId());
            if (producto != null) {
                System.out.println("Producto: " + producto.getNombre() +
                        ", Cantidad: " + detalle.getCantidad() +
                        ", Subtotal: $" + detalle.getSubtotal() +
                        ", Puntos acumulados: " + detalle.calcularPuntos());
            }
        }
    }

    public void reporteDeCompras(int anio, int mes) throws SQLException {
        List<Compra> compras = Compra.obtenerComprasPorFecha(anio, mes);


        List<Compra> comprasPagadas = compras.stream()
                .filter(compra -> compra.getMontoTotal() > 0)
                .toList();

        if (comprasPagadas.isEmpty()) {
            System.out.println("No hay compras pagadas registradas en el año " + anio + " y mes " + mes + ".");
            return;
        }

        System.out.println("Reporte de Compras Pagadas - Año: " + anio + ", Mes: " + mes);
        for (Compra compra : comprasPagadas) {
            Usuario usuario = Usuario.obtenerUsuarioPorId(compra.getEstudianteId());
            if (usuario != null) {
                System.out.println("Usuario: " + usuario.getNombreUsuario() +
                        ", Total Compra: $" + compra.getMontoTotal() +
                        ", Puntos Acumulados: " + compra.getPuntosOtorgados());
            }
        }
    }
}
