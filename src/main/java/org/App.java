package org;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String H2_DATABASE = "testParcial";
    private static final String H2_URL = "jdbc:h2:tcp://localhost/~/" + H2_DATABASE;
    //modificar segun config
    static final String JDBC_USER = "sa";
    static final String JDBC_PASSWORD = "";
    private static Connection connection; // Connection como campo de clase
    private static Double pagarProveedores = 0.0;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(H2_URL, JDBC_USER, JDBC_PASSWORD);
    }

    private static final Logger logger = LogManager.getLogger(App.class);

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
        String createTableProductosSQL = "CREATE TABLE IF NOT EXISTS PRODUCTOS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(20) NOT NULL, "
                + "stock INT NOT NULL,"
                + "createdAt DATE,"
                + "price DOUBLE NOT NULL"
                + ")";
        String createTableVentaSQL = "CREATE TABLE IF NOT EXISTS VENTAS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name_sell VARCHAR(50) NOT NULL, "
                + "quantity INT NOT NULL, "
                + "total_price DOUBLE NOT NULL, "
                + "category VARCHAR(10),"
                + "createdAt DATE"
                + ")";
        String createTablePlatoSQL = "CREATE TABLE IF NOT EXISTS PLATO ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name_plato VARCHAR(50) NOT NULL, "
                + "quantity INT NOT NULL, "
                + "price DOUBLE NOT NULL, "
                + "description VARCHAR(50),"
                + "createdAt DATE"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableProductosSQL);
            statement.execute(createTableVentaSQL);
            statement.execute(createTablePlatoSQL);
            System.out.println("Tabla PRODUCTOS creada exitosamente");
            System.out.println("Tabla VENTA creada exitosamente");
            System.out.println("Tabla PLATOS creada exitosamente");
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla PRODUCTOS: " + e.getMessage());
        }

        // Iniciar la aplicación
        System.out.println(" ----- Bienvenido a Minimarket Homero ----- ");
        while (true) {
            ShowMenu();
        }
    }

    public static void ShowMenu() {
        Scanner sInt = new Scanner(System.in);
        System.out.println("[1] Minimarket");
        System.out.println("[2] Servicio de comida");
        System.out.println("[3] PRUEBA 1 - Platos");
        System.out.println("[0] Salir del programa.");
        int option = sInt.nextInt();
        switch (option) {
            case 1:
                ShowMenuMarket();
                break;
            case 2:
                ShowMenuFood();
                break;
            case 3:
                Prueba1Platos();
                break;
            case 0:
                logger.info("Programa finalizado");
                System.exit(0);
        }
    }

    public static void Prueba1Platos(){
        List<Plato> platosSQL = new ArrayList<>();
        LocalDate localDate = LocalDate.now(); // Obtiene la fecha actual
        Date sqlDate = new Date(System.currentTimeMillis());

        platosSQL.add(new Plato("Paella", "Traditional Spanish dish", 10.0, 15, sqlDate));
        platosSQL.add(new Plato("Tacos", "Mexican dish", 20.0, 9, sqlDate));
        platosSQL.add(new Plato("Sushi", "Japanese dish", 30.0, 12, sqlDate));
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO PLATO (name_plato, quantity, price, description, createdAt) VALUES (?, ?, ?, ?, ?)")) {
            for (Plato p : platosSQL) {
                pstmt.setString(1, p.getName());
                pstmt.setInt(2, p.getQuantity());
                pstmt.setDouble(3, p.getPrice());
                pstmt.setString(4, p.getDescription());
                pstmt.setDate(5, p.getCreatedAt());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar PLATOS: " + e.getMessage());
        }
    }

    public static void ShowMenuMarket() {
        Scanner sInt = new Scanner(System.in);
        //minimarket
        System.out.println("[1] Vender / Cobrar producto");
        System.out.println("[2] Ingreso de mercadería");
        System.out.println("[3] Pago a proveedor");
        System.out.println("[4] Consulta de ventas"); //a- Diaria  b- Mensual
        System.out.println("[5] Balance"); // mostrar ganancias y pérdidas
        System.out.println("[0] Volver.");
        OptionSelectedMarket(sInt.nextInt());
    }

    public static void ShowMenuFood() {
        Scanner sInt = new Scanner(System.in);
        //comida
        System.out.println("[1] Solicitar una comanda a la cocina.");
        System.out.println("[2] Pagar cuenta.");
        System.out.println("[3] Informacion estadística de platos más pedidos.");
        System.out.println("[0] Volver.");
        OptionSelectedFood(sInt.nextInt());
    }

    public static void OptionSelectedMarket(int option) {
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

            case 0:
                ShowMenu();
                break;
            default:
                logger.warn("Opción no válida!!!");
                break;
        }
    }

    public static void OptionSelectedFood(int option) {
        switch (option) {
            case 1:
                logger.info("Solicitar una comanda a la cocina.");
                ComandaCocina();
                break;
            case 2:
                logger.info("Pagar cuenta");
                PagarCuenta();
                break;
            case 3:
                logger.info("Informacion estadística de platos más pedidos");
                PlatosMasPedidos();
                break;
            case 0:
                ShowMenu();
                break;
        }
    }

    /*public void PrintArray(List<?> list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }*/

    public static void CobrarProducto() {
        List<ProductoDto> productosCarrito = new ArrayList<>();
        Ventas sells = new Ventas();
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
                        logger.info("Producto encontrado!");
                        int id = resultSet.getInt("id");
                        String nombre = resultSet.getString("name");
                        double precio = resultSet.getDouble("price");
                        int stock = resultSet.getInt("stock");

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
            pagarProveedores += total;
            sells.setConnection(connection);
            sells.IngresarVentasProductos(productosCarrito);
        } else {
            logger.warn("Compra NO realizada.");
        }
    }

    public static void IngresarProducto() {
        Scanner sn = new Scanner(System.in);
        Scanner sName = new Scanner(System.in);
        Scanner sText = new Scanner(System.in);
        while (true) {
            System.out.print("Ingrese el NOMBRE: ");
            String name = sName.nextLine().toLowerCase();
            //CHECKEAR SI EXISTE
            try {
                String sql = "SELECT * FROM Productos WHERE name = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    ResultSet resultSet = pstmt.executeQuery();
                    if (resultSet.next()) {
                        logger.warn("Este producto ya existe.");
                        IngresarProducto();
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
            }
            //PRECIO
            Double price;
            while (true) {
                System.out.print("Ingrese el PRECIO: ");
                price = sn.nextDouble();
                if (price <= 0.0) {
                    logger.warn("STOCK DEBE SER MAYOR A 0.");
                    continue;
                }
                break;
            }
            //STOCK
            Integer stock = 0;
            while (true) {
                System.out.print("Ingrese el STOCK: ");
                stock = sn.nextInt();
                if (stock <= 0) {
                    logger.warn("STOCK DEBE SER MAYOR A 0.");
                    continue;
                }
                break;
            }

            Producto newProduct = new Producto(name, price, stock);
            //INSERTAR PRODUCTO

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

            System.out.printf("Agregar otro producto? ENTER = SI : ");
            if (sText.nextLine().isEmpty()) {
                sName.nextLine();
                continue;
            }
            break;

        }
        ShowMenuMarket();
    }

    public static void PagarProveedor() {

        Scanner sText = new Scanner(System.in);

        logger.info("TOTAL:" + pagarProveedores);
        System.out.println("1) Pagar.");
        System.out.println("2) Volver.");

        switch (sText.nextInt()) {
            case 1:
                logger.info("PAGO A PROVEEDORES REALIZADO/");
                pagarProveedores = 0.0;
                break;
            case 0:
                ShowMenuMarket();
                break;
            default:
                System.out.println("No se puede realizar la operacion/");
                PagarProveedor();
                break;
        }

    }

    public static void ConsultaVentas() {
        System.out.println("Diaria.");
        System.out.println("Mensual.");
    }

    public static void Balance() {
        System.out.println("Ganancias.");
        System.out.println("Perdida.");
    }

    public static void ComandaCocina() {

    }

    public static void PagarCuenta() {

    }

    public static void PlatosMasPedidos() {

    }
}
