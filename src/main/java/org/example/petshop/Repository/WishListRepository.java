package org.example.petshop.Repository;

import org.example.petshop.Entity.UserEntity;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.WishListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishListEntity, Long> {
    List<WishListEntity> findByUserEntity(UserEntity userEntity);
    List<WishListEntity> findByUserEntityAndProductsEntity(UserEntity userEntity, ProductsEntity productsEntity);
}
