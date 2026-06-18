package org.example.petshop.Api;

import org.example.petshop.DTO.DataPageResponse;
import org.example.petshop.DTO.ImportProductDTO;
import org.example.petshop.DTO.ImportProductRequest;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.Service.ImportProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ImportProductApi {
    @Autowired
    ImportProductService importProductService;

    @GetMapping(value = "/api/admin/import/product")
    public ResponseEntity<Object> getImportProduct(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<ImportProductDTO> importProductDTOS = importProductService.getAllImportProducts(page);
        DataPageResponse dataPageResponse = new DataPageResponse();
        dataPageResponse.setData(importProductDTOS);
        dataPageResponse.setTotalPages(importProductDTOS.getTotalPages());
        dataPageResponse.setCurrentPage(page);
        dataPageResponse.setStatus(HttpStatus.OK);
        dataPageResponse.setMessage("Success");
        dataPageResponse.setData(importProductDTOS.getContent());
        return new ResponseEntity<>(dataPageResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/admin/import/product/id={id}")
    public ResponseEntity<Object> getImportProducts(@PathVariable Long id) {
        Object result = importProductService.getImportProducts(id);
        if(result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/api/admin/import/product/id-product={id}")
    public ResponseEntity<Object> getImportProductByProduct(@PathVariable Long id) {
        Object result = importProductService.getImportProductsByProduct(id);
        if(result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/admin/import/product")
    public ResponseEntity<Object> addImportProduct(@RequestBody ImportProductRequest importProductRequest) {
        MessageDTO messageDTO = importProductService.addImportProduct(importProductRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @PutMapping(value = "/api/admin/import/product")
    public ResponseEntity<Object> updateImportProduct(@RequestBody ImportProductRequest importProductRequest) {
        MessageDTO messageDTO = importProductService.updateImportProduct(importProductRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @DeleteMapping(value = "/api/admin/import/product/id-import={id}")
    public ResponseEntity<Object> deleteImportProduct(@PathVariable Long id) {
        MessageDTO messageDTO = importProductService.deleteImportProduct(id);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }
}
