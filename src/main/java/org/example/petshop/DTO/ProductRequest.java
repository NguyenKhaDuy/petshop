package org.example.petshop.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private Long idProduct;
    private String nameProduct;
    private String description;
    private Long idCategory;
    private List<MultipartFile> images;
    private List<Long> retainedImageIds;
    private Boolean imageSelectionProvided;
}
