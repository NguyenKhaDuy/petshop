package org.example.petshop;

import org.example.petshop.DTO.ProductRequest;
import org.example.petshop.Entity.CategoryEntity;
import org.example.petshop.Entity.ProductImageEntity;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Repository.CategoryRepository;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.SizeProductRepository;
import org.example.petshop.Repository.SizeRepository;
import org.example.petshop.Service.Implement.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductImageUpdateUnitTests {
    @Mock
    ProductRepository productRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    SizeRepository sizeRepository;
    @Mock
    SizeProductRepository sizeProductRepository;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void updateRetainsSelectedImageRemovesDeselectedImageAndAddsNewImage() {
        CategoryEntity category = new CategoryEntity();
        category.setIdCategory(5L);

        ProductsEntity product = new ProductsEntity();
        product.setIdProduct(9L);
        product.setNameProduct("Old name");
        product.setDescription("Old description");
        product.setCategoryEntity(category);
        product.getProductImageEntities().add(image(product, 11L, 1));
        product.getProductImageEntities().add(image(product, 12L, 2));

        when(productRepository.findById(9L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        ProductRequest request = new ProductRequest();
        request.setIdProduct(9L);
        request.setNameProduct("Updated name");
        request.setDescription("Updated description");
        request.setIdCategory(5L);
        request.setImageSelectionProvided(true);
        request.setRetainedImageIds(List.of(11L));
        request.setImages(List.of(new MockMultipartFile(
                "images", "new.png", "image/png", new byte[]{3, 4, 5}
        )));

        assertEquals(HttpStatus.OK, productService.updateProduct(request).getStatus());
        assertEquals(2, product.getProductImageEntities().size());
        assertTrue(product.getProductImageEntities().stream()
                .anyMatch(item -> Long.valueOf(11L).equals(item.getIdImage())));
        assertTrue(product.getProductImageEntities().stream()
                .noneMatch(item -> Long.valueOf(12L).equals(item.getIdImage())));
        assertTrue(product.getProductImageEntities().stream()
                .anyMatch(item -> item.getIdImage() == null && item.getImage()[0] == 3));
        verify(productRepository).save(product);
    }

    @Test
    void updateCanRemoveAllImagesWithoutUploadingNewOnes() {
        CategoryEntity category = new CategoryEntity();
        category.setIdCategory(5L);

        ProductsEntity product = new ProductsEntity();
        product.setIdProduct(9L);
        product.setCategoryEntity(category);
        product.getProductImageEntities().add(image(product, 11L, 1));

        when(productRepository.findById(9L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        ProductRequest request = new ProductRequest();
        request.setIdProduct(9L);
        request.setNameProduct("Product");
        request.setDescription("Description");
        request.setIdCategory(5L);
        request.setImageSelectionProvided(true);
        request.setRetainedImageIds(List.of());

        assertEquals(HttpStatus.OK, productService.updateProduct(request).getStatus());
        assertTrue(product.getProductImageEntities().isEmpty());
    }

    private ProductImageEntity image(ProductsEntity product, Long id, int marker) {
        ProductImageEntity image = new ProductImageEntity();
        image.setIdImage(id);
        image.setImage(new byte[]{(byte) marker});
        image.setProductsEntity(product);
        return image;
    }
}
