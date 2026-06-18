package org.example.petshop.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "size")
public class SizeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSize;

    @Column(name = "size")
    private String size;

    @OneToMany(mappedBy = "sizeEntity", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<SizeProductEntity> sizeProductEntities = new ArrayList<>();

    @OneToMany(mappedBy = "sizeEntity", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<ImportProductEntity> importProductEntities = new ArrayList<>();
}
