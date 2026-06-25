package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.*;
import org.example.petshop.Entity.*;
import org.example.petshop.Repository.CategoryRepository;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.SizeProductRepository;
import org.example.petshop.Repository.SizeRepository;
import org.example.petshop.Service.ProductService;
import org.example.petshop.Utils.ConvertByteToBase64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    SizeRepository sizeRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    SizeProductRepository sizeProductRepository;

    @Override
    public Page<ProductDTO> getProducts(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        Page<ProductsEntity> productsEntities = productRepository.findAll(pageable);
        List<ProductDTO> productDTOs = new ArrayList<>();
        for (ProductsEntity productsEntity : productsEntities) {
            ProductDTO productDTO = new ProductDTO();
            modelMapper.map(productsEntity, productDTO);
            productDTO.setCategoryName(productsEntity.getCategoryEntity().getNameCategory());
            productDTO.setIdCategory(productsEntity.getCategoryEntity().getIdCategory());

            List<ImageDTO> imageDTOS = new ArrayList<>();
            for (ProductImageEntity productImageEntity : productsEntity.getProductImageEntities()){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setIdImage(productImageEntity.getIdImage());
                imageDTO.setImageBase64(ConvertByteToBase64.toBase64(productImageEntity.getImage()));
                imageDTOS.add(imageDTO);
            }
            productDTO.setImageDTOS(imageDTOS);

            List<SizeProductDTO> sizeProductDTOS = new ArrayList<>();
            for (SizeProductEntity sizeProductEntity : productsEntity.getSizeProductEntities()){
                SizeProductDTO sizeProductDTO = new SizeProductDTO();
                sizeProductDTO.setIdSizeProduct(sizeProductEntity.getIdSizeProduct());
                sizeProductDTO.setQuantity(sizeProductEntity.getQuantity());
                sizeProductDTO.setPrice(sizeProductEntity.getPrice());
                sizeProductDTO.setSize(sizeProductEntity.getSizeEntity().getSize());
                sizeProductDTOS.add(sizeProductDTO);
            }
            productDTO.setSizeProductDTOS(sizeProductDTOS);

            List<ImportProduct> importProducts = new ArrayList<>();
            for (ImportProductEntity importProductEntity : productsEntity.getImportProductEntities()){
                ImportProduct importProduct = new ImportProduct();
                importProduct.setIdImportProduct(importProductEntity.getIdImportProduct());
                importProduct.setImportPrice(importProductEntity.getImportPrice());
                importProduct.setQuantity(importProductEntity.getQuantity());
                importProduct.setImportDate(importProductEntity.getImportDate());
                importProduct.setSize(importProductEntity.getSizeEntity().getSize());
                importProducts.add(importProduct);
            }
            productDTO.setImportProducts(importProducts);

            List<ReviewProductDTO> reviewProductDTOS = new ArrayList<>();
            for (ReviewEntity reviewEntity : productsEntity.getReviewEntities()){
                ReviewProductDTO reviewProductDTO = new ReviewProductDTO();
                reviewProductDTO.setIdReview(reviewEntity.getIdReview());
                reviewProductDTO.setNameUser(reviewEntity.getUserEntity().getName());
                reviewProductDTO.setStar(reviewEntity.getStar());
                reviewProductDTO.setCreatedAt(reviewEntity.getCreatedAt());
                reviewProductDTO.setComment(reviewEntity.getComment());
                reviewProductDTOS.add(reviewProductDTO);
            }
            productDTO.setReviewProductDTOS(reviewProductDTOS);

            productDTOs.add(productDTO);
        }
        return new PageImpl<>(productDTOs, productsEntities.getPageable(), productsEntities.getTotalElements());
    }

    @Override
    public DataResponse getProducts() {
        List<ProductDTO> productDTOS = new ArrayList<>();
        DataResponse dataResponse = new DataResponse();
        List<ProductsEntity> productsEntities = productRepository.findAll();
        for (ProductsEntity productsEntity : productsEntities) {
            ProductDTO productDTO = new ProductDTO();
            modelMapper.map(productsEntity, productDTO);
            productDTO.setCategoryName(productsEntity.getCategoryEntity().getNameCategory());
            productDTO.setIdCategory(productsEntity.getCategoryEntity().getIdCategory());

            List<ImageDTO> imageDTOS = new ArrayList<>();
            for (ProductImageEntity productImageEntity : productsEntity.getProductImageEntities()){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setIdImage(productImageEntity.getIdImage());
                imageDTO.setImageBase64(ConvertByteToBase64.toBase64(productImageEntity.getImage()));
                imageDTOS.add(imageDTO);
            }
            productDTO.setImageDTOS(imageDTOS);

            List<SizeProductDTO> sizeProductDTOS = new ArrayList<>();
            for (SizeProductEntity sizeProductEntity : productsEntity.getSizeProductEntities()){
                SizeProductDTO sizeProductDTO = new SizeProductDTO();
                sizeProductDTO.setIdSizeProduct(sizeProductEntity.getIdSizeProduct());
                sizeProductDTO.setQuantity(sizeProductEntity.getQuantity());
                sizeProductDTO.setPrice(sizeProductEntity.getPrice());
                sizeProductDTO.setSize(sizeProductEntity.getSizeEntity().getSize());
                sizeProductDTOS.add(sizeProductDTO);
            }
            productDTO.setSizeProductDTOS(sizeProductDTOS);

            List<ImportProduct> importProducts = new ArrayList<>();
            for (ImportProductEntity importProductEntity : productsEntity.getImportProductEntities()){
                ImportProduct importProduct = new ImportProduct();
                importProduct.setIdImportProduct(importProductEntity.getIdImportProduct());
                importProduct.setImportPrice(importProductEntity.getImportPrice());
                importProduct.setQuantity(importProductEntity.getQuantity());
                importProduct.setImportDate(importProductEntity.getImportDate());
                importProduct.setSize(importProductEntity.getSizeEntity().getSize());
                importProducts.add(importProduct);
            }
            productDTO.setImportProducts(importProducts);

            List<ReviewProductDTO> reviewProductDTOS = new ArrayList<>();
            for (ReviewEntity reviewEntity : productsEntity.getReviewEntities()){
                ReviewProductDTO reviewProductDTO = new ReviewProductDTO();
                reviewProductDTO.setIdReview(reviewEntity.getIdReview());
                reviewProductDTO.setNameUser(reviewEntity.getUserEntity().getName());
                reviewProductDTO.setStar(reviewEntity.getStar());
                reviewProductDTO.setCreatedAt(reviewEntity.getCreatedAt());
                reviewProductDTO.setComment(reviewEntity.getComment());
                reviewProductDTOS.add(reviewProductDTO);
            }
            productDTO.setReviewProductDTOS(reviewProductDTOS);

            productDTOS.add(productDTO);
        }
        dataResponse.setStatus(HttpStatus.OK);
        dataResponse.setMessage("Success");
        dataResponse.setData(productDTOS);
        return dataResponse;
    }

    @Override
    public Object getProductById(Long idProduct) {
        DataResponse dataResponse = new DataResponse();
        MessageDTO messageDTO = new MessageDTO();
        try{
            ProductsEntity productsEntity = productRepository.findById(idProduct).get();
            ProductDTO productDTO = new ProductDTO();
            modelMapper.map(productsEntity, productDTO);
            productDTO.setCategoryName(productsEntity.getCategoryEntity().getNameCategory());
            productDTO.setIdCategory(productsEntity.getCategoryEntity().getIdCategory());

            List<ImageDTO> imageDTOS = new ArrayList<>();
            for (ProductImageEntity productImageEntity : productsEntity.getProductImageEntities()){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setIdImage(productImageEntity.getIdImage());
                imageDTO.setImageBase64(ConvertByteToBase64.toBase64(productImageEntity.getImage()));
                imageDTOS.add(imageDTO);
            }
            productDTO.setImageDTOS(imageDTOS);

            List<SizeProductDTO> sizeProductDTOS = new ArrayList<>();
            for (SizeProductEntity sizeProductEntity : productsEntity.getSizeProductEntities()){
                SizeProductDTO sizeProductDTO = new SizeProductDTO();
                sizeProductDTO.setIdSizeProduct(sizeProductEntity.getIdSizeProduct());
                sizeProductDTO.setQuantity(sizeProductEntity.getQuantity());
                sizeProductDTO.setPrice(sizeProductEntity.getPrice());
                sizeProductDTO.setSize(sizeProductEntity.getSizeEntity().getSize());
                sizeProductDTOS.add(sizeProductDTO);
            }
            productDTO.setSizeProductDTOS(sizeProductDTOS);

            List<ImportProduct> importProducts = new ArrayList<>();
            for (ImportProductEntity importProductEntity : productsEntity.getImportProductEntities()){
                ImportProduct importProduct = new ImportProduct();
                importProduct.setIdImportProduct(importProductEntity.getIdImportProduct());
                importProduct.setImportPrice(importProductEntity.getImportPrice());
                importProduct.setQuantity(importProductEntity.getQuantity());
                importProduct.setImportDate(importProductEntity.getImportDate());
                importProduct.setSize(importProductEntity.getSizeEntity().getSize());
                importProducts.add(importProduct);
            }
            productDTO.setImportProducts(importProducts);

            List<ReviewProductDTO> reviewProductDTOS = new ArrayList<>();
            for (ReviewEntity reviewEntity : productsEntity.getReviewEntities()){
                ReviewProductDTO reviewProductDTO = new ReviewProductDTO();
                reviewProductDTO.setIdReview(reviewEntity.getIdReview());
                reviewProductDTO.setNameUser(reviewEntity.getUserEntity().getName());
                reviewProductDTO.setStar(reviewEntity.getStar());
                reviewProductDTO.setCreatedAt(reviewEntity.getCreatedAt());
                reviewProductDTO.setComment(reviewEntity.getComment());
                reviewProductDTOS.add(reviewProductDTO);
            }
            productDTO.setReviewProductDTOS(reviewProductDTOS);

            dataResponse.setStatus(HttpStatus.OK);
            dataResponse.setMessage("Success");
            dataResponse.setData(productDTO);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product not found");
            return messageDTO;
        }
    }

    @Override
    public Object getProductsByCategory(Long idCategory) {
        DataResponse dataResponse = new DataResponse();
        MessageDTO messageDTO = new MessageDTO();
        List<ProductDTO> productDTOS = new ArrayList<>();
        try{
            CategoryEntity categoryEntity = categoryRepository.findById(idCategory).get();
            List<ProductsEntity> productsEntities = productRepository.findByCategoryEntity(categoryEntity);
            for (ProductsEntity productsEntity : productsEntities) {
                ProductDTO productDTO = new ProductDTO();
                modelMapper.map(productsEntity, productDTO);
                productDTO.setCategoryName(productsEntity.getCategoryEntity().getNameCategory());
                productDTO.setIdCategory(productsEntity.getCategoryEntity().getIdCategory());

                List<ImageDTO> imageDTOS = new ArrayList<>();
                for (ProductImageEntity productImageEntity : productsEntity.getProductImageEntities()){
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setIdImage(productImageEntity.getIdImage());
                    imageDTO.setImageBase64(ConvertByteToBase64.toBase64(productImageEntity.getImage()));
                    imageDTOS.add(imageDTO);
                }
                productDTO.setImageDTOS(imageDTOS);

                List<SizeProductDTO> sizeProductDTOS = new ArrayList<>();
                for (SizeProductEntity sizeProductEntity : productsEntity.getSizeProductEntities()){
                    SizeProductDTO sizeProductDTO = new SizeProductDTO();
                    sizeProductDTO.setIdSizeProduct(sizeProductEntity.getIdSizeProduct());
                    sizeProductDTO.setQuantity(sizeProductEntity.getQuantity());
                    sizeProductDTO.setPrice(sizeProductEntity.getPrice());
                    sizeProductDTO.setSize(sizeProductEntity.getSizeEntity().getSize());
                    sizeProductDTOS.add(sizeProductDTO);
                }
                productDTO.setSizeProductDTOS(sizeProductDTOS);

                List<ImportProduct> importProducts = new ArrayList<>();
                for (ImportProductEntity importProductEntity : productsEntity.getImportProductEntities()){
                    ImportProduct importProduct = new ImportProduct();
                    importProduct.setIdImportProduct(importProductEntity.getIdImportProduct());
                    importProduct.setImportPrice(importProductEntity.getImportPrice());
                    importProduct.setQuantity(importProductEntity.getQuantity());
                    importProduct.setImportDate(importProductEntity.getImportDate());
                    importProduct.setSize(importProductEntity.getSizeEntity().getSize());
                    importProducts.add(importProduct);
                }
                productDTO.setImportProducts(importProducts);

                List<ReviewProductDTO> reviewProductDTOS = new ArrayList<>();
                for (ReviewEntity reviewEntity : productsEntity.getReviewEntities()){
                    ReviewProductDTO reviewProductDTO = new ReviewProductDTO();
                    reviewProductDTO.setIdReview(reviewEntity.getIdReview());
                    reviewProductDTO.setNameUser(reviewEntity.getUserEntity().getName());
                    reviewProductDTO.setStar(reviewEntity.getStar());
                    reviewProductDTO.setCreatedAt(reviewEntity.getCreatedAt());
                    reviewProductDTO.setComment(reviewEntity.getComment());
                    reviewProductDTOS.add(reviewProductDTO);
                }
                productDTO.setReviewProductDTOS(reviewProductDTOS);

                productDTOS.add(productDTO);
            }
            dataResponse.setStatus(HttpStatus.OK);
            dataResponse.setMessage("Success");
            dataResponse.setData(productDTOS);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Category not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addProduct(ProductRequest productRequest) {
        MessageDTO messageDTO = new MessageDTO();
        CategoryEntity categoryEntity = null;
        try {
            categoryEntity = categoryRepository.findById(productRequest.getIdCategory()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Category not found");
            return messageDTO;
        }
        ProductsEntity productsEntity = new ProductsEntity();
        productsEntity.setNameProduct(productRequest.getNameProduct());
        productsEntity.setDescription(productRequest.getDescription());
        productsEntity.setCategoryEntity(categoryEntity);
        productsEntity.setCreatedAt(LocalDateTime.now());
        productsEntity.setUpdatedAt(LocalDateTime.now());
        productsEntity.setStatus("ACTIVE");
        addUploadedImages(productsEntity, productRequest.getImages());
        productRepository.save(productsEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO updateProduct(ProductRequest productRequest) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            ProductsEntity productsEntity = productRepository.findById(productRequest.getIdProduct()).get();
            productsEntity.setNameProduct(productRequest.getNameProduct());
            productsEntity.setDescription(productRequest.getDescription());
            CategoryEntity categoryEntity = categoryRepository.findById(productRequest.getIdCategory()).get();
            productsEntity.setCategoryEntity(categoryEntity);

            boolean hasUploadedImages = productRequest.getImages() != null
                    && productRequest.getImages().stream().anyMatch(image -> image != null && !image.isEmpty());
            if (Boolean.TRUE.equals(productRequest.getImageSelectionProvided())) {
                Set<Long> retainedImageIds = new HashSet<>(
                        productRequest.getRetainedImageIds() != null
                                ? productRequest.getRetainedImageIds()
                                : List.of()
                );
                productsEntity.getProductImageEntities().removeIf(
                        image -> image.getIdImage() == null || !retainedImageIds.contains(image.getIdImage())
                );
                addUploadedImages(productsEntity, productRequest.getImages());
            } else if (hasUploadedImages) {
                productsEntity.getProductImageEntities().clear();
                addUploadedImages(productsEntity, productRequest.getImages());
            }
            productsEntity.setUpdatedAt(LocalDateTime.now());
            productRepository.save(productsEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product not found");
            return messageDTO;
        }
    }

    private void addUploadedImages(ProductsEntity product, List<MultipartFile> images) {
        if (images == null) {
            return;
        }
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }
            ProductImageEntity productImageEntity = new ProductImageEntity();
            productImageEntity.setProductsEntity(product);
            try {
                productImageEntity.setImage(image.getBytes());
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
            product.getProductImageEntities().add(productImageEntity);
        }
    }

    @Override
    public MessageDTO deleteProduct(Long idProduct) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            ProductsEntity productsEntity = productRepository.findById(idProduct).get();
            productRepository.delete(productsEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addSizeProduct(SizeProductRequest sizeProductRequest) {
        MessageDTO messageDTO = new MessageDTO();
        ProductsEntity productsEntity = null;
        SizeEntity sizeEntity = null;
        try{
            sizeEntity = sizeRepository.findById(sizeProductRequest.getIdSize()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Size not found");
            return messageDTO;
        }
        try {
            productsEntity = productRepository.findById(sizeProductRequest.getIdProduct()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product not found");
            return messageDTO;
        }
        SizeProductEntity sizeProductEntity = new SizeProductEntity();
        sizeProductEntity.setSizeEntity(sizeEntity);
        sizeProductEntity.setProductsEntity(productsEntity);
        sizeProductEntity.setQuantity(sizeProductRequest.getQuantity());
        sizeProductEntity.setPrice(sizeProductRequest.getPrice());
        sizeProductRepository.save(sizeProductEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO updateSizeProduct(SizeProductRequest sizeProductRequest) {
        MessageDTO messageDTO = new MessageDTO();
        ProductsEntity productsEntity = null;
        SizeEntity sizeEntity = null;
        try{
            sizeEntity = sizeRepository.findById(sizeProductRequest.getIdSize()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Size not found");
            return messageDTO;
        }
        try {
            productsEntity = productRepository.findById(sizeProductRequest.getIdProduct()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product not found");
            return messageDTO;
        }
        SizeProductEntity sizeProductEntity = sizeProductRepository.findBySizeEntityAndProductsEntity(sizeEntity, productsEntity);
        sizeProductEntity.setQuantity(sizeProductRequest.getQuantity());
        sizeProductEntity.setPrice(sizeProductRequest.getPrice());
        sizeProductRepository.save(sizeProductEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO deleteSizeProduct(Long idSizeProduct) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            SizeProductEntity sizeProductEntity = sizeProductRepository.findById(idSizeProduct).get();
            sizeProductRepository.delete(sizeProductEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Size product not found");
            return messageDTO;
        }
    }
}
