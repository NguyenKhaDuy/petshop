package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.CategoryDTO;
import org.example.petshop.DTO.CategoryRequest;
import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.Entity.CategoryEntity;
import org.example.petshop.Repository.CategoryRepository;
import org.example.petshop.Service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;


    @Override
    public DataResponse getAllCategories() {
        DataResponse dataResponse = new DataResponse();
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            CategoryDTO categoryDTO = new CategoryDTO();
            modelMapper.map(categoryEntity, categoryDTO);
            categoryDTOS.add(categoryDTO);
        }
        dataResponse.setData(categoryDTOS);
        dataResponse.setMessage("Success");
        dataResponse.setStatus(HttpStatus.OK);
        return dataResponse;
    }

    @Override
    public Object getCategoryById(Long idCategory) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try {
            CategoryEntity categoryEntity = categoryRepository.findById(idCategory).get();
            CategoryDTO categoryDTO = new CategoryDTO();
            modelMapper.map(categoryEntity, categoryDTO);
            dataResponse.setData(categoryDTO);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Category not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addCategory(CategoryRequest categoryRequest) {
        MessageDTO messageDTO = new MessageDTO();
        CategoryEntity categoryEntity = new CategoryEntity();
        modelMapper.map(categoryRequest, categoryEntity);
        categoryEntity.setUpdatedAt(LocalDateTime.now());
        categoryEntity.setCreatedAt(LocalDateTime.now());
        categoryRepository.save(categoryEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO updateCategory(CategoryRequest categoryRequest) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            CategoryEntity categoryEntity = categoryRepository.findById(categoryRequest.getIdCategory()).get();
            modelMapper.map(categoryRequest, categoryEntity);
            categoryEntity.setUpdatedAt(LocalDateTime.now());
            categoryRepository.save(categoryEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Category not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO deleteCategory(Long idCategory) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            CategoryEntity categoryEntity = categoryRepository.findById(idCategory).get();
            categoryRepository.delete(categoryEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Category not found");
            return messageDTO;
        }
    }
}
