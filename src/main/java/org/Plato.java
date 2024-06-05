package org;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class Plato {
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Date createdAt;
}
