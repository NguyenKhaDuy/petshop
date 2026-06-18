package org.example.petshop.Repository;

import org.example.petshop.Entity.CartItemEnity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEnity, Long> {
}
