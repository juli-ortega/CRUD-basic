package org;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ventas {
    private static final Logger logger = LogManager.getLogger(App.class);

    //private List<Platos>meal = new ArrayList();
    private Connection connection;

    public void IngresarVentasProductos(List<ProductoDto> productosCarritos) {
        try {
            for (ProductoDto e : productosCarritos) {
                String sql = "INSERT INTO Ventas (name, price, stock) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, e.getName());
                    pstmt.setDouble(2, e.getPrice());
                    pstmt.setInt(3, e.getStock());
                    pstmt.executeUpdate();
                    logger.info("Producto insertado a VENTAS correctamente.");
                    logger.info(e.toString());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta SQL: " + e.getMessage());
        }

    }

}
