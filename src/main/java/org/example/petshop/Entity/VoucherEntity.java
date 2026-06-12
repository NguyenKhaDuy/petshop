package org.example.petshop.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "voucher")
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVoucher;

    @Column(name = "code")
    private String code;

    @Column(name = "discount")
    private Long discount;

    @Column(name = "expired_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiredDate;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "created_at")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "voucherEntity", fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<OrderEntity> orderEntities = new ArrayList<>();
}
