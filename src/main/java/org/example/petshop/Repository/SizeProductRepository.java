package org.example.petshop.Repository;

import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.SizeEntity;
import org.example.petshop.Entity.SizeProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeProductRepository extends JpaRepository<SizeProductEntity, Long> {
    SizeProductEntity findBySizeEntityAndProductsEntity(SizeEntity sizeEntity, ProductsEntity productsEntity);
}
