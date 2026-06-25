package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.*;
import org.example.petshop.Entity.ImportProductEntity;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.SizeEntity;
import org.example.petshop.Repository.ImportProductRepository;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.SizeRepository;
import org.example.petshop.Service.ImportProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ImportProductServiceImpl implements ImportProductService {
    @Autowired
    ImportProductRepository importProductRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SizeRepository sizeRepository;


    @Override
    public Page<ImportProductDTO> getAllImportProducts(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        Page<ImportProductEntity> importProductEntities = importProductRepository.findAll(pageable);
        List<ImportProductDTO> importProductDTOS = new ArrayList<>();
        for (ImportProductEntity importProductEntity : importProductEntities) {
            ImportProductDTO importProductDTO = new ImportProductDTO();
            importProductDTO.setIdImportProduct(importProductEntity.getIdImportProduct());
            importProductDTO.setIdProduct(importProductEntity.getProductsEntity().getIdProduct());
            importProductDTO.setProductName(importProductEntity.getProductsEntity().getNameProduct());
            importProductDTO.setIdSize(importProductEntity.getSizeEntity().getIdSize());
            importProductDTO.setSize(importProductEntity.getSizeEntity().getSize());
            importProductDTO.setImportDate(importProductEntity.getImportDate());
            importProductDTO.setImportPrice(importProductEntity.getImportPrice());
            importProductDTO.setQuantity(importProductEntity.getQuantity());
            importProductDTOS.add(importProductDTO);
        }
        return new PageImpl<>(importProductDTOS, importProductEntities.getPageable(), importProductEntities.getTotalElements());
    }

    @Override
    public Object getImportProducts(Long idImportProduct) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try{
            ImportProductEntity importProductEntity = importProductRepository.findById(idImportProduct).get();
            ImportProductDTO importProductDTO = new ImportProductDTO();
            importProductDTO.setIdImportProduct(importProductEntity.getIdImportProduct());
            importProductDTO.setIdProduct(importProductEntity.getProductsEntity().getIdProduct());
            importProductDTO.setProductName(importProductEntity.getProductsEntity().getNameProduct());
            importProductDTO.setIdSize(importProductEntity.getSizeEntity().getIdSize());
            importProductDTO.setSize(importProductEntity.getSizeEntity().getSize());
            importProductDTO.setImportDate(importProductEntity.getImportDate());
            importProductDTO.setImportPrice(importProductEntity.getImportPrice());
            importProductDTO.setQuantity(importProductEntity.getQuantity());

            dataResponse.setData(importProductDTO);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Import Product Not Found");
            return messageDTO;
        }
    }

    @Override
    public Object getImportProductsByProduct(Long idProduct) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try{
            ProductsEntity productsEntity = productRepository.findById(idProduct).get();
            List<ImportProductEntity> importProductEntities = importProductRepository.findByProductsEntity(productsEntity);
            List<ImportProductDTO> importProductDTOS = new ArrayList<>();
            for (ImportProductEntity importProductEntity : importProductEntities) {
                ImportProductDTO importProductDTO = new ImportProductDTO();
                importProductDTO.setIdImportProduct(importProductEntity.getIdImportProduct());
                importProductDTO.setIdProduct(importProductEntity.getProductsEntity().getIdProduct());
                importProductDTO.setProductName(importProductEntity.getProductsEntity().getNameProduct());
                importProductDTO.setIdSize(importProductEntity.getSizeEntity().getIdSize());
                importProductDTO.setSize(importProductEntity.getSizeEntity().getSize());
                importProductDTO.setImportDate(importProductEntity.getImportDate());
                importProductDTO.setImportPrice(importProductEntity.getImportPrice());
                importProductDTO.setQuantity(importProductEntity.getQuantity());
                importProductDTOS.add(importProductDTO);
            }
            dataResponse.setData(importProductDTOS);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product Not Found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addImportProduct(ImportProductRequest importProductRequest) {
        MessageDTO messageDTO = new MessageDTO();
        SizeEntity sizeEntity = null;
        ProductsEntity productsEntity = null;
        try{
            sizeEntity = sizeRepository.findById(importProductRequest.getIdSize()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Size Not Found");
            return messageDTO;
        }
        try{
            productsEntity = productRepository.findById(importProductRequest.getIdProduct()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product Not Found");
            return messageDTO;
        }
        ImportProductEntity importProduct = new ImportProductEntity();
        importProduct.setProductsEntity(productsEntity);
        importProduct.setSizeEntity(sizeEntity);
        importProduct.setImportPrice(importProductRequest.getImportPrice());
        importProduct.setQuantity(importProductRequest.getQuantity());
        importProduct.setImportDate(importProductRequest.getImportDate());
        importProductRepository.save(importProduct);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO updateImportProduct(ImportProductRequest importProductRequest) {
        MessageDTO messageDTO = new MessageDTO();
        SizeEntity sizeEntity = null;
        ProductsEntity productsEntity = null;
        try{
            sizeEntity = sizeRepository.findById(importProductRequest.getIdSize()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Size Not Found");
            return messageDTO;
        }
        try{
            productsEntity = productRepository.findById(importProductRequest.getIdProduct()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Product Not Found");
            return messageDTO;
        }
        try {
            ImportProductEntity importProductEntity = importProductRepository.findByProductsEntityAndSizeEntity(productsEntity, sizeEntity);
            if (importProductEntity == null) {
                messageDTO.setStatus(HttpStatus.NOT_FOUND);
                messageDTO.setMessage("Import Product Not Found");
                return messageDTO;
            }
            importProductEntity.setSizeEntity(sizeEntity);
            importProductEntity.setImportPrice(importProductRequest.getImportPrice());
            importProductEntity.setQuantity(importProductRequest.getQuantity());
            importProductEntity.setImportDate(importProductRequest.getImportDate());
            importProductRepository.save(importProductEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Import Product Not Found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO deleteImportProduct(Long idImportProduct) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            ImportProductEntity importProductEntity = importProductRepository.findById(idImportProduct).get();
            importProductRepository.delete(importProductEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Import Product Not Found");
            return messageDTO;
        }
    }
}
