package org;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ventas {
    private static final Logger logger = LogManager.getLogger(App.class);
    private Connection connection;

    public void IngresarVentasProductos(List<ProductoDto> productosCarritos) {
        Date sqlDate = new Date(System.currentTimeMillis());
        try {
            for (ProductoDto e : productosCarritos) {
                String sql = "INSERT INTO VENTAS (name_sell, quantity, total_price, category, createdAt) VALUES (?,?,?,?,?);";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, e.getName());
                    pstmt.setInt(2, e.getCantidad());
                    pstmt.setDouble(3, e.getPrice());
                    pstmt.setString(4, "PRODUCTO");
                    pstmt.setDate(5, sqlDate);
                    pstmt.executeUpdate();
                    logger.info("Producto insertado a VENTAS correctamente.");
                    logger.info(e.toString());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }

    public void IngresarVentasPlatos(List<ProductoDto> platosCarritos) {
        Date sqlDate = new Date(System.currentTimeMillis());
        try {
            for (ProductoDto e : platosCarritos) {
                String sql = "INSERT INTO VENTAS (name_sell, quantity, total_price, category, createdAt) VALUES (?,?,?,?,?);";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, e.getName());
                    pstmt.setInt(2, e.getCantidad());
                    pstmt.setDouble(3, e.getPrice());
                    pstmt.setString(4, "PLATO");
                    pstmt.setDate(5, sqlDate);
                    pstmt.executeUpdate();
                    logger.info("Plato insertado a VENTAS correctamente.");
                    logger.info(e.toString());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }
    }
}


/*
String createTableVentaSQL = "CREATE TABLE IF NOT EXISTS VENTAS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name_sell VARCHAR(50) NOT NULL, "
                + "quantity INT NOT NULL, "
                + "total_price DOUBLE NOT NULL, "
                + "category VARCHAR(10),"
                + "createdAt DATE"
                + ")";
 */