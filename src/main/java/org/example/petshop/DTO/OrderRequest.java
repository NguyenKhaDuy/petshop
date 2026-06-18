package org.example.petshop.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long idUser;
    private String note;
    private Long idPaymentMethod;
    private Long idInformationOrder;
    private List<OrderItemRequest> orderItemRequests;
    private Long idVoucher;
}
