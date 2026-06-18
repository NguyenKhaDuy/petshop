package org.example.petshop.Api;

import org.example.petshop.DTO.CategoryDTO;
import org.example.petshop.DTO.CategoryRequest;
import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryApi {
    @Autowired
    CategoryService categoryService;

    @GetMapping(value = "/api/category")
    public ResponseEntity<Object> getCategory() {
        DataResponse dataResponse = categoryService.getAllCategories();
        return new ResponseEntity<>(dataResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/admin/category/id-cate={id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable("id") Long id) {
        Object result = categoryService.getCategoryById(id);
        if (result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/admin/category")
    public ResponseEntity<Object> addCategory(@RequestBody CategoryRequest categoryRequest) {
        MessageDTO messageDTO = categoryService.addCategory(categoryRequest);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/api/admin/category")
    public ResponseEntity<Object> updateCategory(@RequestBody CategoryRequest categoryRequest) {
        MessageDTO messageDTO = categoryService.updateCategory(categoryRequest);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/admin/category/id-cate={id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("id") Long id) {
        MessageDTO messageDTO = categoryService.deleteCategory(id);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

}
