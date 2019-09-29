package demo.ecommerce.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import demo.ecommerce.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

    // product cost price
    @JsonIgnore
    @Column("unit_cost_price")
    private Double unitCostPrice;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // product selling price
    @Column("unit_price")
    private Double unitPrice;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    // (unit-price * quantity) + shipping
    @Column("total_price")
    private Double totalPrice;

    // calculated from any external sources
    @Column("shipping_cost")
    private Double shippingCost = 0.0;

}
