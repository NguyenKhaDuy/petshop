package org.example.petshop.Service;

import org.example.petshop.DTO.ImportProductDTO;
import org.example.petshop.DTO.ImportProductRequest;
import org.example.petshop.DTO.MessageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ImportProductService {
    Page<ImportProductDTO> getAllImportProducts(Integer page);
    Object getImportProducts(Long idImportProduct);
    Object getImportProductsByProduct(Long idProduct);
    MessageDTO addImportProduct(ImportProductRequest importProductRequest);
    MessageDTO updateImportProduct(ImportProductRequest importProductRequest);
    MessageDTO deleteImportProduct(Long idImportProduct);
}
