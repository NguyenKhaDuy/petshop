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
public class OrderDetailDTO {
    private Long idOrderDetail;
    private Integer quantity;
    private Double totalPrice;
    private Double price;
    private String nameProduct;
    private Long idProduct;
    private String imageProduct;
    private String size;
}
