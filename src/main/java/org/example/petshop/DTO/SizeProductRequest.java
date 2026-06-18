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
public class SizeProductRequest {
    private Long idSizeProduct;
    private Long idProduct;
    private Long idSize;
    private Double price;
    private Long quantity;
}
