package org.example;
import org.example.controller.UsuarioController;
import org.example.controller.CompraController;
import org.example.controller.ProductoController;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UsuarioController usuarioController = new UsuarioController();
        CompraController compraController = new CompraController();
        ProductoController productoController = new ProductoController();
        Scanner scanner = new Scanner(System.in);

        try {
            // Login del usuario
            System.out.print("Ingrese nombre de usuario: ");
            String nombreUsuario = scanner.nextLine();

            System.out.print("Ingrese documento: ");
            int documento = scanner.nextInt();

            if (usuarioController.login(nombreUsuario, documento)) {
                System.out.println("Login exitoso. Opciones disponibles:");

                boolean continuar = true;
                while (continuar) {
                    // Menú de opciones
                    System.out.println("\nMenú de Opciones:");
                    System.out.println("1. Iniciar Compra");
                    System.out.println("2. Agregar Producto a Compra");
                    System.out.println("3. Listar Líneas de la Compra");
                    System.out.println("4. Eliminar Línea de la Compra");
                    System.out.println("5. Modificar Cantidad de Producto en Línea");
                    System.out.println("6. Totalizar Compra");
                    System.out.println("7. Pagar Compra");
                    System.out.println("8. Consultar Compra Almacenada");
                    System.out.println("9. Reporte de Compras");
                    System.out.println("10. Salir");
                    System.out.print("Seleccione una opción: ");
                    int opcion = scanner.nextInt();

                    switch (opcion) {
                        case 1:
                            // Iniciar compra
                            int estudianteId = usuarioController.getEstudianteId();
                            if (estudianteId != -1) {
                                compraController.iniciarCompra(estudianteId);
                            } else {
                                System.out.println("Error: usuario no autenticado.");
                            }
                            break;

                        case 2:
                            // Agregar producto a compra
                            System.out.print("Ingrese el ID de la compra a la que desea agregar productos: ");
                            int compraId = scanner.nextInt();
                            compraController.agregarProducto(compraId, productoController);
                            compraController.totalizarCompra(compraId); // Totalizar después de agregar productos
                            break;

                        case 3:
                            // Listar líneas de la compra
                            System.out.print("Ingrese el ID de la compra para listar sus líneas: ");
                            int compraIdConsulta = scanner.nextInt();
                            compraController.listarLineasDeCompra(compraIdConsulta);
                            break;

                        case 4:
                            // Eliminar línea de la compra
                            System.out.print("Ingrese el ID de la compra para eliminar una línea: ");
                            int compraIdEliminar = scanner.nextInt();
                            compraController.eliminarLineaDeCompra(compraIdEliminar);
                            break;

                        case 5:
                            // Modificar cantidad de producto en línea
                            System.out.print("Ingrese el ID de la compra para modificar una línea: ");
                            int compraIdModificar = scanner.nextInt();
                            compraController.modificarCantidadDeProductoEnLinea(compraIdModificar);
                            break;

                        case 6:
                            // Totalizar compra
                            System.out.print("Ingrese el ID de la compra para totalizar: ");
                            int compraIdTotalizar = scanner.nextInt();
                            compraController.totalizarCompra(compraIdTotalizar);
                            break;

                        case 7:
                            // Pagar compra
                            System.out.print("Ingrese el ID de la compra a pagar: ");
                            int compraIdPagar = scanner.nextInt();
                            compraController.pagarCompra(compraIdPagar);
                            break;

                        case 8:
                            // Consultar compra almacenada
                            System.out.print("Ingrese el ID de la compra que desea consultar: ");
                            int compraIdConsultaAlmacenada = scanner.nextInt();
                            compraController.consultarCompra(compraIdConsultaAlmacenada);
                            break;

                        case 9:
                            // Reporte de compras
                            System.out.print("Ingrese el año (YYYY): ");
                            int anio = scanner.nextInt();
                            System.out.print("Ingrese el mes (MM): ");
                            int mes = scanner.nextInt();
                            compraController.reporteDeCompras(anio, mes);
                            break;

                        case 10:
                            // Salir
                            continuar = false;
                            System.out.println("Saliendo del sistema...");
                            break;

                        default:
                            System.out.println("Opción no válida. Intente nuevamente.");
                            break;
                    }
                }
            } else {
                System.out.println("Credenciales inválidas. Intente nuevamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión con la base de datos: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
