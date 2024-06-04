package org;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:tcp://localhost/~/test1";
    //modificar segun config
    static final String JDBC_USER = "sa";
    static final String JDBC_PASSWORD = "";
    private static Connection connection; // Connection como campo de clase

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(H2_URL, JDBC_USER, JDBC_PASSWORD);
    }

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        // Crear la conexión a la base de datos
        try {
            Class.forName(JDBC_DRIVER);
            connection = getConnection();
            System.out.println("Conexión a la base de datos exitosa");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
            return;
        }

        // Crear la tabla PRODUCTOS si no existe
        String createTableSQL = "CREATE TABLE IF NOT EXISTS PRODUCTOS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(20) NOT NULL, "
                + "stock INT NOT NULL,"
                + "price DOUBLE NOT NULL"
                + ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Tabla PRODUCTOS creada exitosamente");
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla PRODUCTOS: " + e.getMessage());
        }

        // Iniciar la aplicación
        Scanner sInt = new Scanner(System.in);

        System.out.println(" ----- Bienvenido a Minimarket Homero ----- ");
        while (true) {
            ShowMenu();
            int option = sInt.nextInt();
            OptionSelected(option);
        }
    }


    public static void ShowMenu() {
        System.out.println("[1] Vender / Cobrar producto");
        System.out.println("[2] Ingreso de mercadería");
        System.out.println("[3] Pago a proveedor");
        System.out.println("[4] Consulta de ventas"); //a- Diaria  b- Mensual
        System.out.println("[5] Balance"); // mostrar ganancias y pérdidas
        System.out.println("[6] Solicitar una comanda a la cocina.");
        System.out.println("[7] Pagar cuenta.");
        System.out.println("[8] Informacion estadística de platos más pedidos.");
        System.out.println("[9] Pruebas.");
        System.out.println("[0] Salir.");
    }

    public static void OptionSelected(int option) {
        switch (option) {
            case 1:
                logger.info("Vender / Cobrar producto");
                CobrarProducto();
                break;
            case 2:
                logger.info("Ingreso de mercadería");
                IngresarProducto();
                break;
            case 3:
                logger.info("Pago a proveedor");
                PagarProveedor();
                break;
            case 4:
                logger.info("Consulta de ventas");
                ConsultaVentas();
                break;
            case 5:
                logger.info("Balance");
                Balance();
                break;
            case 6:
                logger.info("Solicitar una comanda a la cocina.");
                ComandaCocina();
                break;
            case 7:
                logger.info("Pagar cuenta");
                PagarCuenta();
                break;
            case 8:
                logger.info("Informacion estadística de platos más pedidos");
                PlatosMasPedidos();
                break;
            case 9:
                String prueba = "  ";
                logger.info("Prueba de " + prueba);
                break;
            case 0:
                logger.info("Saliendo del programa. Adios!");
                System.exit(0);
                break;
            default:
                logger.warn("Opción no válida!!!");
                break;
        }
    }

    public void PrintArray(List<?> list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }

    public static void CobrarProducto() {
        List<ProductoDto> productosCarrito = new ArrayList<>();
        Scanner sText = new Scanner(System.in);
        Scanner sInt = new Scanner(System.in);

        while (true) {
            System.out.println("Nombre del producto a cobrar: ");
            String name = sText.nextLine();

            try {
                String sql = "SELECT * FROM Productos WHERE name = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    ResultSet resultSet = pstmt.executeQuery();

                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String nombre = resultSet.getString("name");
                        double precio = resultSet.getDouble("price");
                        int stock = resultSet.getInt("stock");

                        logger.info("Producto encontrado!");
                        if (stock <= 0) {
                            throw new Error("No hay más stock de este producto");
                        }
                        System.out.println("Nombre: " + nombre);
                        System.out.println("Precio: " + precio);
                        System.out.println("Stock: " + stock);
                        System.out.println("Ingrese cantidad: ");
                        int cantidad = sInt.nextInt();
                        ProductoDto prodToSell = new ProductoDto(nombre, precio, cantidad, stock);
                        logger.info(prodToSell);
                        productosCarrito.add(prodToSell);
                    } else {
                        System.out.println("No se encontró el producto con el nombre: " + name);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
            }

            System.out.printf("Agregar otro producto? ENTER = SI : ");
            if (sText.nextLine().isEmpty()) {
                continue;
            }
            break;
        }
        Double total = 0.0;
        for (ProductoDto e : productosCarrito) {
            System.out.println("Nombre: " + e.getName() + " | " + "Precio: " + e.getPrice() + " | Cantidad: " + e.getCantidad());
            total += e.getPrice() * e.getCantidad();
        }
        System.out.println("El total es $" + total);
        System.out.print("CONFIRMAR COMPRA (S/N) : ");
        String confirm = sText.nextLine().toLowerCase();
        if (confirm.equals("s")) {
            for (ProductoDto element : productosCarrito) {
                try {
                    String updateSql = "UPDATE Productos SET stock = ? WHERE name = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, (element.getStock() - element.getCantidad()));
                        updateStmt.setString(2, element.getName());
                        updateStmt.executeUpdate();
                        logger.info("Stock actualizado correctamente");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
                }
            }
            logger.info("Compra realizada!");
        } else {
            logger.warn("Compra NO realizada.");
        }
    }

    public static void IngresarProducto() {

        Scanner sName = new Scanner(System.in);
        Scanner sn = new Scanner(System.in);
        System.out.print("Ingrese el NOMBRE: ");
        String name = sName.nextLine().toLowerCase();
        System.out.print("Ingrese el PRECIO: ");
        Double price = sn.nextDouble();
        System.out.print("Ingrese el STOCK: ");
        Integer stock = sn.nextInt();

        Producto newProduct = new Producto(name, price, stock);

        try {
            String sql = "INSERT INTO Productos (name, price, stock) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, newProduct.getName());
                pstmt.setDouble(2, newProduct.getPrice());
                pstmt.setInt(3, newProduct.getStock());
                pstmt.executeUpdate();
                logger.info("Producto insertado correctamente.");
                logger.info(newProduct.toString());
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }


    public static void PagarProveedor() {

    }

    public static void ConsultaVentas() {
        System.out.println("Diaria");
        System.out.println("Mensual");
    }

    public static void Balance() {
        System.out.println("Ganancias");
        System.out.println("Perdida");
    }

    public static void ComandaCocina() {

    }

    public static void PagarCuenta() {

    }

    public static void PlatosMasPedidos() {

    }
}