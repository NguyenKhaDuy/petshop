package org.example.petshop.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {
    private Long idVoucher;
    private String code;
    private Long discount;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiredDate;
    private Long quantity;
}
