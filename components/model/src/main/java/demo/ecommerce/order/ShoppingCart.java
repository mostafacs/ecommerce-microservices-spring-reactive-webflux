package demo.ecommerce.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

/**
 * @Author Mostafa Albana
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("shopping_cart")
public class ShoppingCart {

    @Id
    private Long id;

    @Column("total_quantity")
    private Integer totalQuantity;

    @Column("sub_total_price")
    private Double subTotalPrice;

    @Column("total_shipping_cost")
    private Double totalShippingCost;

    @Column("total_cost")
    private Double totalCost;

    @Column("user_id")
    private Long userId;

    @Transient
    private List<ShoppingCartItem> cartItemList;

}
