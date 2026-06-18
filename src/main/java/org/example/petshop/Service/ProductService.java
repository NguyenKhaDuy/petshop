package org.example.petshop.Service;

import org.example.petshop.DTO.*;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductDTO> getProducts(Integer page);
    DataResponse getProducts();
    Object getProductById(Long idProduct);
    Object getProductsByCategory(Long idCategory);
    MessageDTO addProduct(ProductRequest productRequest);
    MessageDTO updateProduct(ProductRequest productRequest);
    MessageDTO deleteProduct(Long idProduct);
    MessageDTO addSizeProduct(SizeProductRequest sizeProductRequest);
    MessageDTO updateSizeProduct(SizeProductRequest sizeProductRequest);
    MessageDTO deleteSizeProduct(Long idSizeProduct);
}
