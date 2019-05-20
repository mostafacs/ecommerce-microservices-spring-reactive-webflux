package com.ecommerce.common.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCart {

    @Id
    private Long id;
    private Integer totalQuantity;
    private BigDecimal subTotalPrice;
    private BigDecimal totalShippingCost;
    private BigDecimal totalCost;
    private List<ShoppingCartItem> cartItemList;

}
