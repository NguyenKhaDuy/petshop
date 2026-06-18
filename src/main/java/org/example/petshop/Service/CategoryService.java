package org.example.petshop.Service;
import org.example.petshop.DTO.CategoryRequest;
import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;

public interface CategoryService {
    DataResponse getAllCategories();
    Object getCategoryById(Long idCategory);
    MessageDTO addCategory(CategoryRequest categoryRequest);
    MessageDTO updateCategory(CategoryRequest categoryRequest);
    MessageDTO deleteCategory(Long idCategory);
}
