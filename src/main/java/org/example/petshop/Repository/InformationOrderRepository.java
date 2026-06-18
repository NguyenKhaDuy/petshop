package org.example.petshop.Repository;

import org.example.petshop.Entity.InformationOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationOrderRepository extends JpaRepository<InformationOrderEntity, Long> {
}
