package org.example.petshop.Repository;

import org.example.petshop.Entity.CategoryEntity;
import org.example.petshop.Entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductsEntity, Long> {
    List<ProductsEntity> findByCategoryEntity(CategoryEntity categoryEntity);
}
