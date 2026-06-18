package org.example.petshop.Repository;

import org.example.petshop.Entity.ImportProductEntity;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportProductRepository extends JpaRepository<ImportProductEntity, Long> {
    List<ImportProductEntity> findByProductsEntity(ProductsEntity productsEntity);
    ImportProductEntity findByProductsEntityAndSizeEntity(ProductsEntity productsEntity, SizeEntity sizeEntity);
}
