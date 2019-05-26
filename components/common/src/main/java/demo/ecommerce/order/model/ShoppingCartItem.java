package demo.ecommerce.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import demo.ecommerce.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("shopping_cart_item")
public class ShoppingCartItem {

    @Id
    private Long id;

    @Transient
    private Product product;

    @JsonIgnore
    @Column("product_id")
    private Long productId;

    @JsonIgnore
    @Column("shopping_cart_id")
    private Long shoppingCartId;

    private Integer quantity;

    @Column("unit_price")
    private Double unitPrice;

    @Column("total_price")
    private Double totalPrice;

    @Column("shipping_cost")
    private Double shippingCost;

}
