package org.example.petshop.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long idCartItem;
    private Integer quantity;
    private Long idSize;
    private String size;
    private Long idProduct;
    private String productName;
    private String imageProduct;
    private Double totalPrice;
}
