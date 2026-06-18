package org.example.petshop.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long idOrder;
    private Double totalAmount;
    private String note;
    private String status;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long idUser;
    private String nameUser;
    private InformationOrderDTO informationOrderDTO;
    private String paymentMethod;
    private String voucherCode;
    private List<OrderDetailDTO> orderDetailDTOS;
}
