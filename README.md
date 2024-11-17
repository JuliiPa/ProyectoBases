# ProyectoBases

## Descripción del Proyecto

ProyectoBases es una aplicación de gestión de compras para estudiantes. Permite a los usuarios autenticarse, realizar compras, agregar productos a las compras y procesar pagos. La aplicación está desarrollada en Java y utiliza una base de datos Oracle para almacenar la información.

## Estructura del Proyecto

El proyecto está organizado en los siguientes paquetes:

- `org.example.controller`: Contiene los controladores de la aplicación.
- `org.example.db`: Contiene la clase para la conexión a la base de datos.
- `org.example.model`: Contiene las clases del modelo de datos.

## Instalación

### Prerrequisitos

- Java Development Kit (JDK) 8 o superior
- Apache Maven
- Oracle Database

### Clonar el Repositorio

```bash
git clone https://github.com/JuliiPa/ProyectoBases.git
cd ProyectoBases
```

# Configuración de la Base de Datos

## Crear la Base de Datos
1. Crear una base de datos en Oracle.
2. Ejecutar los scripts SQL para crear las tablas necesarias: 
   - `PB_COMPRA`
   - `PB_DETALLE_COMPRA`
   - `PB_PRODUCTO`
   - etc.

## Configurar la Conexión a la Base de Datos
Modificar la clase `DatabaseConnection` en el archivo:  
`src/main/java/org/example/db/DatabaseConnection.java`  
Asegúrate de actualizar las credenciales de conexión:

```java
private static final String URL = "jdbc:oracle:thin:@<HOST>:<PORT>/<SERVICE_NAME>";
private static final String USER = "<USERNAME>";
private static final String PASSWORD = "<PASSWORD>";
```

## Compilar y Ejecutar el Proyecto

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="org.example.Main"
```

## Uso

### Autenticación de Usuario
Para autenticar a un usuario, se utiliza el método login de la clase UsuarioController:
```bash
uarioController usuarioController = new UsuarioController();
boolean isAuthenticated = usuarioController.login("nombreUsuario", 123456);
```

### Crear una Compra
Para crear una nueva compra, se utiliza la clase Compra:
```bash
Compra compra = new Compra(estudianteId, true);
compra.crearCompra();
```

### Agregar Productos a la Compra
Para agregar productos a una compra existente:
```bash
compra.agregarProducto(productoId, cantidad);
```

### Calcular el Total de la Compra
Para calcular el total de una compra:
```bash
double total = compra.calcularTotal();
```

### Procesar el Pago
Para procesar el pago de una compra:
```bash
compra.procesarPago();
```

### Obtener Compras por Fecha
Para obtener una lista de compras realizadas en un mes específico:
```bash
List<Compra> compras = Compra.obtenerComprasPorFecha(anio, mes);
```
