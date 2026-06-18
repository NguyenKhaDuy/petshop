package org.example.petshop.Service;

import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.OrderDTO;
import org.example.petshop.DTO.OrderRequest;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<OrderDTO> getOrders(Integer page);
    Object getOrderByUser(Long idUser);
    MessageDTO order(OrderRequest orderRequest);
    MessageDTO updateStatusOrder(Long idOrder, String status);
    Object getOrderById(Long idOrder);
    MessageDTO deleteOrder(Long idOrder);
}
