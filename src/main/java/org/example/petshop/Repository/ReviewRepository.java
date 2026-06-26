package org.example.petshop.Repository;

import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.ReviewEntity;
import org.example.petshop.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Page<ReviewEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ReviewEntity> findByUserEntityOrderByCreatedAtDesc(UserEntity userEntity);

    List<ReviewEntity> findByUserEntityAndProductsEntity(UserEntity userEntity, ProductsEntity productsEntity);
}
