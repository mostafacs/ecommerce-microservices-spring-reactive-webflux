package demo.ecommerce.model.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;


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

    @Column("user_id")
    private Long userId;

    @Column("created_on")
    private Date createdOn;

    @Column("updated_on")
    private Date updatedOn;

    @Transient
    public void increaseInventory(int amount) {
        inventoryCounts += amount;
    }

    @Transient
    public void decreaseInventory(int amount) {
        inventoryCounts -= amount;
    }

}
