package org.example.petshop.Service.Implement;
import org.example.petshop.DTO.AddWishListRequest;
import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.WishListDTO;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.UserEntity;
import org.example.petshop.Entity.WishListEntity;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.UserRepository;
import org.example.petshop.Repository.WishListRepository;
import org.example.petshop.Service.WishListService;
import org.example.petshop.Utils.ConvertByteToBase64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WishListServiceImpl implements WishListService {
    @Autowired
    WishListRepository wishListRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ProductRepository productRepository;

    @Override
    public Object getAllWishListByUser(Long idUser) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try{
            UserEntity userEntity = userRepository.findById(idUser).get();
            List<WishListEntity> wishListEntities = wishListRepository.findByUserEntity(userEntity);
            List<WishListDTO> wishListDTOS = new ArrayList<>();
            for (WishListEntity wishListEntity : wishListEntities) {
                WishListDTO wishListDTO = new WishListDTO();
                modelMapper.map(wishListEntity, wishListDTO);
                wishListDTO.setIdProduct(wishListEntity.getProductsEntity().getIdProduct());
                wishListDTO.setProductName(wishListEntity.getProductsEntity().getNameProduct());
                wishListDTO.setImageProduct(ConvertByteToBase64.toBase64(wishListEntity.getProductsEntity().getProductImageEntities().get(0).getImage()));
                wishListDTOS.add(wishListDTO);
            }
            dataResponse.setMessage("Successfully retrieved wish list");
            dataResponse.setData(wishListDTOS);
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Can not found user");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addWishList(AddWishListRequest addWishListRequest) {
        MessageDTO messageDTO = new MessageDTO();
        UserEntity userEntity =  null;
        ProductsEntity productsEntity = null;
        try{
            userEntity = userRepository.findById(addWishListRequest.getIdUser()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Can not found user");
            return messageDTO;
        }
        try {
            productsEntity = productRepository.findById(addWishListRequest.getIdProduct()).get();
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Can not found product");
            return messageDTO;
        }
        WishListEntity wishListEntity = new WishListEntity();
        wishListEntity.setProductsEntity(productsEntity);
        wishListEntity.setUserEntity(userEntity);
        wishListEntity.setCreatedAt(LocalDateTime.now());
        wishListRepository.save(wishListEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Successfully added wish list");
        return messageDTO;
    }

    @Override
    public MessageDTO deleteWishList(Long idWishList) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            WishListEntity wishListEntity = wishListRepository.findById(idWishList).get();
            wishListRepository.delete(wishListEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Successfully deleted wish list");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Can not found wish list");
            return messageDTO;
        }
    }
}
