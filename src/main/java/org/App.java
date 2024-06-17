package org;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String H2_DATABASE = "test1";
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

    public static void main(String[] args) throws SQLException {
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

    public static void ShowMenu() throws SQLException {
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

    public static void Prueba1Platos() {
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

    public static void ShowMenuMarket() throws SQLException {
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

    public static void ShowMenuFood() throws SQLException {
        Scanner sInt = new Scanner(System.in);
        //comida
        System.out.println("[1] Solicitar una comanda a la cocina.");
        System.out.println("[2] Informacion estadística de platos más pedidos.");
        System.out.println("[0] Volver.");
        OptionSelectedFood(sInt.nextInt());
    }

    public static void OptionSelectedMarket(int option) throws SQLException {
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
                ConsultarVentas();
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

    public static void OptionSelectedFood(int option) throws SQLException {
        switch (option) {
            case 1:
                logger.info("Solicitar una comanda a la cocina.");
                ComandaCocina();
                break;
            case 2:
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
                        int cantidad = 0;
                        while (true) {
                            cantidad = sInt.nextInt();
                            if (cantidad == 0 || cantidad > stock) {
                                logger.warn("Ingrese cantidad correctamente.");
                                continue;
                            }
                            break;
                        }
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

    public static void IngresarProducto() throws SQLException {
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

    public static void PagarProveedor() throws SQLException {

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

    public static void ConsultarVentas() throws SQLException {
        Scanner stext = new Scanner(System.in);
        while (true) {
            System.out.println("[D]iaria o [M]ensual? ");
            String period = stext.nextLine().toLowerCase();
            if (period.equals("m")) {
                List<String> monthlySales = getSales(connection, period);
                for (String e : monthlySales) {
                    System.out.println(e);
                }
                break;
            } else if (period.equals("d")) {
                List<String> dailySales = getSales(connection, period);
                for (String e : dailySales) {
                    System.out.println(e.toString());
                }
                break;
            }
            continue;
        }

    }

    public static List<String> getSales(Connection connection, String period) throws SQLException {
        String queryDailySales = "SELECT p.name AS product_name, v.category, COUNT(*) AS sales_count " +
                "FROM VENTAS v " +
                "JOIN PRODUCTOS p ON v.name_sell = p.name " +
                "WHERE v.createdAt = CURDATE() AND v.category = 'PRODUCTO' " +
                "GROUP BY p.name, v.category";
        String queryMonthlySales = "SELECT p.name AS product_name, v.category, COUNT(*) AS sales_count " +
                "FROM VENTAS v " +
                "JOIN PRODUCTOS p ON v.name_sell = p.name " +
                "WHERE MONTH(v.createdAt) = MONTH(CURDATE()) AND YEAR(v.createdAt) = YEAR(CURDATE()) AND v.category = 'PRODUCTO' " +
                "GROUP BY p.name, v.category";

        String query = period.equals("daily") ? queryDailySales : queryMonthlySales;

        List<String> sales = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String productName = rs.getString("product_name");
                String category = rs.getString("category");
                int count = rs.getInt("sales_count");
                sales.add("Product: " + productName + ", Category: " + category + ", Sales Count: " + count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sales;
    }


    public static void Balance() throws SQLException {

        double totalProfit = calculateProfitAndLoss(connection);
        System.out.println("Total Profit: " + totalProfit);
    }

    public static double calculateProfitAndLoss(Connection connection) throws SQLException {
        double totalProfit = calculateProductProfit(connection);
        double totalLoss = calculatePlatoCost(connection);

        double netProfit = totalProfit - totalLoss;

        System.out.println("Total Profit: " + totalProfit);
        System.out.println("Total Loss: " + totalLoss);
        System.out.println("Net Profit: " + netProfit);
        return totalProfit;
    }

    public static double calculateProductProfit(Connection connection) throws SQLException {
        String query = "SELECT SUM(total_price) AS total_sales FROM VENTAS WHERE category = 'PRODUCTO'";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_sales");
            }
        }
        return 0.0;
    }

    public static double calculatePlatoCost(Connection connection) throws SQLException {
        String query = "SELECT SUM(price * quantity) AS total_cost FROM PLATO";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_cost");
            }
        }
        return 0.0;
    }


    public static void ComandaCocina() {
        ArrayList<Plato> comanda = new ArrayList<>();
        Double total = 0.0;
        try (Statement stmt = connection.createStatement()) {
            Scanner l = new Scanner(System.in);
            while (true) {
                ResultSet resultSet = stmt.executeQuery("SELECT * FROM plato");
                List<Plato> platosDisponibles = new ArrayList<>();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name_plato");
                    int quantity = resultSet.getInt("quantity");
                    double price = resultSet.getDouble("price");
                    String description = resultSet.getString("description");
                    Date createdAt = resultSet.getDate("createdAt");

                    Plato plato = new Plato(name, description, price, quantity, createdAt);
                    platosDisponibles.add(plato);
                }

                logger.info("PLATOS DISPONIBLES");
                for (Plato plato : platosDisponibles) {
                    logger.info("Plato " + (platosDisponibles.indexOf(plato) + 1) + ":\n" +
                            "Nombre: " + plato.getName() + "\n" +
                            "Descripción: " + plato.getDescription() + "\n" +
                            "Precio: " + plato.getPrice() + "\n" +
                            "Cantidad: " + plato.getQuantity() + "\n" +
                            "Fecha de creación: " + plato.getCreatedAt() + "\n"
                    );
                }
                int numero;
                int cantPlatos;
                while (true) {
                    logger.info("Elija el numero del plato ");
                    numero = l.nextInt();
                    if (numero > 3 || numero < 1) {
                        continue;
                    }
                    logger.info("Cuantos platos de este queres?: ");
                    cantPlatos = l.nextInt();
                    if (cantPlatos < 1) {
                        continue;
                    }
                    break;
                }
                logger.info("Confirmar plato (S/N): ");
                String confirmacion = l.next();

                if (confirmacion.equalsIgnoreCase("S")) {
                    logger.info("Plato confirmado");
                    Plato platoApasar = platosDisponibles.get(numero - 1);
                    platoApasar.setQuantity(cantPlatos);
                    platoApasar.setPrice(cantPlatos * platosDisponibles.get(numero - 1).getPrice());
                    comanda.add(platoApasar);
                    System.out.println("Comanda actual: ");
                    for (Plato e : comanda) {
                        System.out.println("Nombre: " + e.getName());
                        System.out.println("Precio: " + e.getPrice());
                        System.out.println("Cantidad: " + e.getQuantity());
                    }
                    total += platosDisponibles.get(numero - 1).getPrice() * cantPlatos;
                } else {
                    logger.info("Plato cancelado");
                }
                logger.info("Pedir mas comida ? (S/N): ");
                String c = l.next();

                if (c.equalsIgnoreCase("N")) {
                    logger.info("Comanda confimada");
                    for (Plato e : comanda) {
                        System.out.println("Nombre: " + e.getName());
                        System.out.println("Precio: " + e.getPrice());
                    }
                    logger.info("Total: " + total);
                    break;
                }
            }
            PagarCuenta(comanda);

        } catch (SQLException e) {
            System.out.println("Error al obtener los platos: " + e.getMessage());
        }

    }

    public static void PagarCuenta(ArrayList<Plato> comanda) {
        Ventas ventaPlato = new Ventas();
        Scanner l = new Scanner(System.in);
        System.out.println("¿Desea pagar la cuenta? (S/N)");
        String confirmacion = l.next();
        while (true) {
            if (confirmacion.equalsIgnoreCase("S")) {
                ventaPlato.setConnection(connection);
                ventaPlato.IngresarVentasPlatos(comanda);
                break;
            } else if (confirmacion.equalsIgnoreCase("n")) {
                System.out.println("La comanda fue eliminada");
                break;
            }
        }

    }


    public static void PlatosMasPedidos() {
        String query = "SELECT * FROM VENTAS WHERE CATEGORY = 'PLATO'";
        int plato1 = 0;
        int plato2 = 0;
        int plato3 = 0;

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name_sell");
                int quantity = rs.getInt("quantity");
                if (name.equals("Paella")) {
                    plato1 += quantity;
                } else if (name.equals("Tacos")) {
                    plato2 += quantity;
                } else if (name.equals("Sushi")) {
                    plato3 += quantity;
                }
            }
            String mostOrderedPlato = getMostOrderedPlato(plato1, plato2, plato3);
            logger.info("El plato mas vendido es: " + mostOrderedPlato);
        } catch (SQLException e) {
            System.out.println("Error al cargar PLATOS: " + e.getMessage());
        }
    }

    private static String getMostOrderedPlato(int paellaCount, int tacosCount, int sushiCount) {
        if (paellaCount > tacosCount && paellaCount > sushiCount) {
            return "Paella";
        } else if (tacosCount > paellaCount && tacosCount > sushiCount) {
            return "Tacos";
        } else if (sushiCount > paellaCount && sushiCount > tacosCount) {
            return "Sushi";
        } else {
            return "Empate entre platos más pedidos";
        }
    }


}