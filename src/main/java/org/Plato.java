package org;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data @AllArgsConstructor @NoArgsConstructor
public class Plato {
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Date createdAt;

}
