package org;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.App.connection;
import static org.App.logger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ventas {
    private List<ProductoDto>products;
    //private List<Platos>meal = new ArrayList();

    public void IngresarVentasProductos(List<ProductoDto> productosCarritos){
        this.products = productosCarritos;

        try {
            String sql = "INSERT INTO Ventas (name, price, stock) VALUES (?, ?, ?)";
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

}
