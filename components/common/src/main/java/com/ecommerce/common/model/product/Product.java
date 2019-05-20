package com.ecommerce.common.model.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("product")
public class Product {

    @Id
    private Long id;

    private String sku;

    private String title;

    @Column("inventory_count")
    private Integer inventoryCounts;

}
