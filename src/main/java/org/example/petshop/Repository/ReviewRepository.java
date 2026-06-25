package org.example.petshop.Repository;

import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.ReviewEntity;
import org.example.petshop.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    ReviewEntity findByUserEntityAndProductsEntity(UserEntity userEntity, ProductsEntity productsEntity);
}
