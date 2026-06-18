package org.example.petshop.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "import_product")
public class ImportProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImportProduct;

    @Column(name = "import_price")
    private Double importPrice;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "import_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime importDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_product")
    private ProductsEntity productsEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_size")
    private SizeEntity sizeEntity;
}
