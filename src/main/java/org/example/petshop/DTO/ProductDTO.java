package org.example.petshop.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long idProduct;
    private String nameProduct;
    private String description;
    private String status;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long idCategory;
    private String categoryName;
    private List<ImageDTO> imageDTOS;
    private List<SizeProductDTO> sizeProductDTOS;
    private List<ImportProduct> importProducts;
    private List<ReviewProductDTO> reviewProductDTOS;
}
