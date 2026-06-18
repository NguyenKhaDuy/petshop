package org.example.petshop.Api;

import org.example.petshop.DTO.*;
import org.example.petshop.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductApi {
    @Autowired
    ProductService productService;

    @GetMapping(value = "/api/admin/product")
    public ResponseEntity<Object> getProducts(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        DataPageResponse dataPageResponse = new DataPageResponse();
        Page<ProductDTO> productDTOS = productService.getProducts(page);
        dataPageResponse.setData(productDTOS.getContent());
        dataPageResponse.setMessage("Success");
        dataPageResponse.setCurrentPage(page);
        dataPageResponse.setTotalPages(productDTOS.getTotalPages());
        dataPageResponse.setStatus(HttpStatus.OK);
        return new ResponseEntity<>(dataPageResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/product")
    public ResponseEntity<Object> getProducts() {
        DataResponse dataResponse = productService.getProducts();
        return new ResponseEntity<>(dataResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/product/id-product={id}")
    public ResponseEntity<Object> getProductById(@PathVariable("id") Long id) {
        Object result = productService.getProductById(id);
        if(result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/api/product/id-cate={id}")
    public ResponseEntity<Object> getProductByCate(@PathVariable("id") Long id) {
        Object result = productService.getProductsByCategory(id);
        if(result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/admin/product")
    public ResponseEntity<Object> addProduct(@ModelAttribute ProductRequest productRequest) {
        MessageDTO messageDTO = productService.addProduct(productRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @PutMapping(value = "/api/admin/product")
    public ResponseEntity<Object> updateProduct(@ModelAttribute ProductRequest productRequest) {
        MessageDTO messageDTO = productService.updateProduct(productRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @DeleteMapping(value = "/api/admin/product/id-product={id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable("id") Long id) {
        MessageDTO messageDTO = productService.deleteProduct(id);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @PostMapping(value = "/api/admin/product/size")
    public ResponseEntity<Object> addSizeProduct(@RequestBody SizeProductRequest sizeProductRequest) {
        MessageDTO messageDTO = productService.addSizeProduct(sizeProductRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @PutMapping(value = "/api/admin/product/size")
    public ResponseEntity<Object> updateSizeProduct(@RequestBody SizeProductRequest sizeProductRequest) {
        MessageDTO messageDTO = productService.updateSizeProduct(sizeProductRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @DeleteMapping(value = "/api/admin/product/size/id-size-product={id}")
    public ResponseEntity<Object> deleteSizeProduct(@PathVariable("id") Long id) {
        MessageDTO messageDTO = productService.deleteSizeProduct(id);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }
}
