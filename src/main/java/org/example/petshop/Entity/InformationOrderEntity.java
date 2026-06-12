package org.example.petshop.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "information_order")
public class InformationOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInformationOrder;

    @Column(name = "address_order")
    private String addressOrder;

    @Column(name = "name_order")
    private String nameOrder;

    @Column(name = "phone_order")
    private String phoneOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "informationOrderEntity", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<OrderEntity> orderEntities = new ArrayList<>();
}
