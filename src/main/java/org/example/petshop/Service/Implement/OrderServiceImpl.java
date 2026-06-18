package org.example.petshop.Service.Implement;

import jakarta.persistence.criteria.Order;
import org.example.petshop.DTO.*;
import org.example.petshop.Entity.*;
import org.example.petshop.Repository.*;
import org.example.petshop.Service.OrderService;
import org.example.petshop.Utils.ConvertByteToBase64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SizeRepository sizeRepository;
    @Autowired
    SizeProductRepository sizeProductRepository;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    InformationOrderRepository informationOrderRepository;

    @Override
    public Page<OrderDTO> getOrders(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        Page<OrderEntity> orderEntities = orderRepository.findAll(pageable);
        List<OrderDTO> orderDTOS = new ArrayList<>();
        for (OrderEntity orderEntity : orderEntities) {
            OrderDTO orderDTO = new OrderDTO();
            modelMapper.map(orderEntity, orderDTO);
            orderDTO.setIdUser(orderEntity.getUserEntity().getIdUser());
            orderDTO.setNameUser(orderEntity.getUserEntity().getName());
            orderDTO.setPaymentMethod(orderEntity.getPaymentMethodEntity().getMethod());
            orderDTO.setVoucherCode(orderEntity.getVoucherEntity().getCode());

            InformationOrderDTO informationOrderDTO = new InformationOrderDTO();
            modelMapper.map(orderEntity.getInformationOrderEntity(), informationOrderDTO);
            orderDTO.setInformationOrderDTO(informationOrderDTO);

            List<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();
            for (OrderDetailEntity orderDetailEntity : orderEntity.getOrderDetailEntities()) {
                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                modelMapper.map(orderDetailEntity, orderDetailDTO);
                orderDetailDTO.setPrice(orderDetailEntity.getSizeProductEntity().getPrice());
                orderDetailDTO.setNameProduct(orderDetailEntity.getSizeProductEntity().getProductsEntity().getNameProduct());
                orderDetailDTO.setIdProduct(orderDetailEntity.getSizeProductEntity().getProductsEntity().getIdProduct());
                orderDetailDTO.setImageProduct(ConvertByteToBase64.toBase64(orderDetailEntity.getSizeProductEntity().getProductsEntity().getProductImageEntities().get(0).getImage()));
                orderDetailDTO.setSize(orderDetailEntity.getSizeProductEntity().getSizeEntity().getSize());
                orderDetailDTOS.add(orderDetailDTO);
            }
            orderDTO.setOrderDetailDTOS(orderDetailDTOS);
            orderDTOS.add(orderDTO);
        }
        return new PageImpl<>(orderDTOS, orderEntities.getPageable(), orderEntities.getTotalElements());
    }

    @Override
    public Object getOrderByUser(Long idUser) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try{
            UserEntity userEntity = userRepository.findById(idUser).get();
            List<OrderEntity> orderEntities = orderRepository.findByUserEntity(userEntity);
            List<OrderDTO> orderDTOS = new ArrayList<>();
            for (OrderEntity orderEntity : orderEntities) {
                OrderDTO orderDTO = new OrderDTO();
                modelMapper.map(orderEntity, orderDTO);
                orderDTO.setIdUser(orderEntity.getUserEntity().getIdUser());
                orderDTO.setNameUser(orderEntity.getUserEntity().getName());
                orderDTO.setPaymentMethod(orderEntity.getPaymentMethodEntity().getMethod());
                orderDTO.setVoucherCode(orderEntity.getVoucherEntity().getCode());

                InformationOrderDTO informationOrderDTO = new InformationOrderDTO();
                modelMapper.map(orderEntity.getInformationOrderEntity(), informationOrderDTO);
                orderDTO.setInformationOrderDTO(informationOrderDTO);

                List<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();
                for (OrderDetailEntity orderDetailEntity : orderEntity.getOrderDetailEntities()) {
                    OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                    modelMapper.map(orderDetailEntity, orderDetailDTO);
                    orderDetailDTO.setPrice(orderDetailEntity.getSizeProductEntity().getPrice());
                    orderDetailDTO.setNameProduct(orderDetailEntity.getSizeProductEntity().getProductsEntity().getNameProduct());
                    orderDetailDTO.setIdProduct(orderDetailEntity.getSizeProductEntity().getProductsEntity().getIdProduct());
                    orderDetailDTO.setImageProduct(ConvertByteToBase64.toBase64(orderDetailEntity.getSizeProductEntity().getProductsEntity().getProductImageEntities().get(0).getImage()));
                    orderDetailDTO.setSize(orderDetailEntity.getSizeProductEntity().getSizeEntity().getSize());
                    orderDetailDTOS.add(orderDetailDTO);
                }
                orderDTO.setOrderDetailDTOS(orderDetailDTOS);
                orderDTOS.add(orderDTO);
            }
            dataResponse.setData(orderDTOS);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("User not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO order(OrderRequest orderRequest) {
        MessageDTO messageDTO = new MessageDTO();
        UserEntity userEntity = null;
        PaymentMethodEntity paymentMethodEntity = null;
        VoucherEntity voucherEntity = null;
        InformationOrderEntity informationOrderEntity = null;
        Double totalPrice = 0.0;
        try{
            userEntity = userRepository.findById(orderRequest.getIdUser()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("User not found");
            return messageDTO;
        }
        try{
            paymentMethodEntity = paymentMethodRepository.findById(orderRequest.getIdPaymentMethod()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Payment method not found");
            return messageDTO;
        }
        try{
            voucherEntity = voucherRepository.findById(orderRequest.getIdVoucher()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Voucher not found");
            return messageDTO;
        }
        try{
            informationOrderEntity = informationOrderRepository.findById(orderRequest.getIdInformationOrder()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("information order not found");
            return messageDTO;
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserEntity(userEntity);
        orderEntity.setPaymentMethodEntity(paymentMethodEntity);
        orderEntity.setInformationOrderEntity(informationOrderEntity);
        orderEntity.setNote(orderRequest.getNote());
        orderEntity.setStatus("WAITING CONFIRMATION");
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setUpdatedAt(LocalDateTime.now());

        List<OrderDetailEntity> orderDetailEntities = new ArrayList<>();
        for (OrderItemRequest orderItemRequest : orderRequest.getOrderItemRequests()){
            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
            ProductsEntity productsEntity = productRepository.findById(orderItemRequest.getIdProduct()).get();
            SizeEntity sizeEntity = sizeRepository.findById(orderItemRequest.getIdSize()).get();
            SizeProductEntity sizeProductEntity = sizeProductRepository.findBySizeEntityAndProductsEntity(sizeEntity, productsEntity);
            orderDetailEntity.setSizeProductEntity(sizeProductEntity);
            orderDetailEntity.setOrderEntity(orderEntity);
            orderDetailEntity.setQuantity(orderItemRequest.getQuantity());
            orderDetailEntity.setTotalPrice(orderItemRequest.getQuantity() * sizeProductEntity.getPrice());

            totalPrice += (orderItemRequest.getQuantity() * sizeProductEntity.getPrice());
            orderDetailEntities.add(orderDetailEntity);
        }

        orderEntity.setOrderDetailEntities(orderDetailEntities);

        if (voucherEntity != null && LocalDate.now().isBefore(voucherEntity.getExpiredDate()) && voucherEntity.getQuantity() > 0){
            orderEntity.setVoucherEntity(voucherEntity);
            orderEntity.setTotalAmount(totalPrice - ((totalPrice * voucherEntity.getDiscount())/100));

            //set lại số lượng của voucher
            Long quantityVoucher = (voucherEntity.getQuantity() - 1);
            voucherEntity.setQuantity(quantityVoucher);
            voucherRepository.save(voucherEntity);
        }else {
            orderEntity.setVoucherEntity(null);
            orderEntity.setTotalAmount(totalPrice);
        }
        orderRepository.save(orderEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO updateStatusOrder(Long idOrder, String status) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            OrderEntity orderEntity = orderRepository.findById(idOrder).get();
            orderEntity.setStatus(status);
            orderRepository.save(orderEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Order not found");
            return messageDTO;
        }
    }

    @Override
    public Object getOrderById(Long idOrder) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try {
            OrderEntity orderEntity = orderRepository.findById(idOrder).get();
            OrderDTO orderDTO = new OrderDTO();
            modelMapper.map(orderEntity, orderDTO);
            orderDTO.setIdUser(orderEntity.getUserEntity().getIdUser());
            orderDTO.setNameUser(orderEntity.getUserEntity().getName());
            orderDTO.setPaymentMethod(orderEntity.getPaymentMethodEntity().getMethod());
            orderDTO.setVoucherCode(orderEntity.getVoucherEntity().getCode());

            InformationOrderDTO informationOrderDTO = new InformationOrderDTO();
            modelMapper.map(orderEntity.getInformationOrderEntity(), informationOrderDTO);
            orderDTO.setInformationOrderDTO(informationOrderDTO);

            List<OrderDetailDTO> orderDetailDTOS = new ArrayList<>();
            for (OrderDetailEntity orderDetailEntity : orderEntity.getOrderDetailEntities()) {
                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                modelMapper.map(orderDetailEntity, orderDetailDTO);
                orderDetailDTO.setPrice(orderDetailEntity.getSizeProductEntity().getPrice());
                orderDetailDTO.setNameProduct(orderDetailEntity.getSizeProductEntity().getProductsEntity().getNameProduct());
                orderDetailDTO.setIdProduct(orderDetailEntity.getSizeProductEntity().getProductsEntity().getIdProduct());
                orderDetailDTO.setImageProduct(ConvertByteToBase64.toBase64(orderDetailEntity.getSizeProductEntity().getProductsEntity().getProductImageEntities().get(0).getImage()));
                orderDetailDTO.setSize(orderDetailEntity.getSizeProductEntity().getSizeEntity().getSize());
                orderDetailDTOS.add(orderDetailDTO);
            }
            orderDTO.setOrderDetailDTOS(orderDetailDTOS);
            dataResponse.setMessage("Success");
            dataResponse.setData(orderDTO);
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Order not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO deleteOrder(Long idOrder) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            OrderEntity orderEntity = orderRepository.findById(idOrder).get();
            orderRepository.delete(orderEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Order not found");
            return messageDTO;
        }
    }
}
