package org.example.petshop.Api;

import org.example.petshop.DTO.DataPageResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.OrderDTO;
import org.example.petshop.DTO.OrderRequest;
import org.example.petshop.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderApi {
    @Autowired
    OrderService orderService;

    @GetMapping(value = "/api/admin/orders")
    public ResponseEntity<Object> getAllOrders(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<OrderDTO> orderDTOS = orderService.getOrders(page);
        DataPageResponse dataPageResponse = new DataPageResponse();
        dataPageResponse.setData(orderDTOS.getContent());
        dataPageResponse.setCurrentPage(page);
        dataPageResponse.setTotalPages(orderDTOS.getTotalPages());
        dataPageResponse.setStatus(HttpStatus.OK);
        dataPageResponse.setMessage("Success");
        return new ResponseEntity<>(dataPageResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/order/user/id-user={id}")
    public ResponseEntity<Object> getOrderByUser(@PathVariable("id") Long id) {
        Object result = orderService.getOrderByUser(id);
        if (result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/order")
    public ResponseEntity<Object> addOrder(@RequestBody OrderRequest orderRequest) {
        MessageDTO messageDTO = orderService.order(orderRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @PutMapping(value = "/api/order/status")
    public ResponseEntity<Object> updateOrderStatus(@RequestParam Long idOrder, @RequestParam String status) {
        MessageDTO messageDTO = orderService.updateStatusOrder(idOrder, status);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @GetMapping(value = "/api/order/id-order={id}")
    public ResponseEntity<Object> getOrderById(@PathVariable("id") Long id) {
        Object result = orderService.getOrderById(id);
        if (result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/order/id-order={id}")
    public ResponseEntity<Object> deleteOrderById(@PathVariable("id") Long id) {
        MessageDTO messageDTO = orderService.deleteOrder(id);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }
}
