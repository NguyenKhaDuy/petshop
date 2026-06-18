package org.example.petshop.Repository;

import jdk.jfr.Registered;
import org.example.petshop.Entity.OrderEntity;
import org.example.petshop.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Registered
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserEntity(UserEntity userEntity);
}
