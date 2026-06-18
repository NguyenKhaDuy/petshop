package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.*;
import org.example.petshop.Entity.*;
import org.example.petshop.Repository.*;
import org.example.petshop.Service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    SizeRepository sizeRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    SizeProductRepository sizeProductRepository;
    @Autowired
    CartItemRepository cartItemRepository;


    @Override
    public Object getCartByUser(Long idUser) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        UserEntity userEntity = null;
        CartEntity cartEntity = null;
        try{
            userEntity = userRepository.findById(idUser).get();
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Can not find user");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        try{
            cartEntity = cartRepository.findByUserEntity(userEntity);
            CartDTO cartDTO = new CartDTO();
            modelMapper.map(cartEntity, cartDTO);
            List<CartItemDTO> cartItemDTOS = new ArrayList<>();
            for (CartItemEnity cartItemEnity : cartEntity.getCartItemEnities()){
                CartItemDTO cartItemDTO = new CartItemDTO();
                cartItemDTO.setIdCartItem(cartItemEnity.getIdCartItem());
                cartItemDTO.setQuantity(cartItemEnity.getQuantity());
                cartItemDTO.setTotalPrice(cartItemEnity.getTotalPrice());
                cartItemDTO.setIdProduct(cartItemEnity.getSizeProductEntity().getProductsEntity().getIdProduct());
                cartItemDTO.setProductName(cartItemEnity.getSizeProductEntity().getProductsEntity().getNameProduct());
                cartItemDTO.setIdSize(cartItemEnity.getSizeProductEntity().getSizeEntity().getIdSize());
                cartItemDTO.setSize(cartItemEnity.getSizeProductEntity().getSizeEntity().getSize());
                cartItemDTOS.add(cartItemDTO);
            }
            cartDTO.setCartItems(cartItemDTOS);
            dataResponse.setData(cartDTO);
            dataResponse.setStatus(HttpStatus.OK);
            dataResponse.setMessage("Success");
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Can not find cart");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addProductToCart(AddProductToCart addProductToCart) {
        MessageDTO messageDTO = new MessageDTO();
        UserEntity userEntity = null;
        CartEntity cartEntity = null;
        ProductsEntity productsEntity = null;
        SizeEntity sizeEntity = null;
        try{
            userEntity = userRepository.findById(addProductToCart.getIdUser()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Can not find user");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        try{
            productsEntity = productRepository.findById(addProductToCart.getProductId()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Can not find product");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        try{
            sizeEntity = sizeRepository.findById(addProductToCart.getIdSize()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Can not find size");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        try{
            cartEntity = cartRepository.findByUserEntity(userEntity);
            CartItemEnity cartItemEnity = new CartItemEnity();
            SizeProductEntity sizeProductEntity = sizeProductRepository.findBySizeEntityAndProductsEntity(sizeEntity, productsEntity);
            cartItemEnity.setSizeProductEntity(sizeProductEntity);
            cartItemEnity.setCartEntity(cartEntity);
            cartItemEnity.setTotalPrice(addProductToCart.getQuantity() * sizeProductEntity.getPrice());
            cartItemEnity.setQuantity(addProductToCart.getQuantity());
            cartEntity.getCartItemEnities().add(cartItemEnity);
            cartRepository.save(cartEntity);
            messageDTO.setMessage("Success");
            messageDTO.setStatus(HttpStatus.OK);
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Can not find cart");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
    }

    @Override
    public MessageDTO removeProductFromCart(Long idCartItem) {
        MessageDTO messageDTO = new MessageDTO();
        try{
            CartItemEnity cartItemEnity = cartItemRepository.findById(idCartItem).get();
            cartItemRepository.delete(cartItemEnity);
            messageDTO.setMessage("Success");
            messageDTO.setStatus(HttpStatus.OK);
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Can not find cart item");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
    }
}
