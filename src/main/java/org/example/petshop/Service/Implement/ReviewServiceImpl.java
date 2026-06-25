package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.ReviewItemRequest;
import org.example.petshop.DTO.ReviewRequest;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.ReviewEntity;
import org.example.petshop.Entity.UserEntity;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.ReviewRepository;
import org.example.petshop.Repository.UserRepository;
import org.example.petshop.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public MessageDTO addReview(ReviewRequest reviewRequest) {
        MessageDTO messageDTO = new MessageDTO();
        if (reviewRequest.getReviewItemRequests() == null) {
            messageDTO.setStatus(HttpStatus.BAD_REQUEST);
            messageDTO.setMessage("Review requests cannot be empty");
            return messageDTO;
        }

        for (ReviewItemRequest reviewItemRequest : reviewRequest.getReviewItemRequests()) {
            ProductsEntity productsEntity = null;
            UserEntity userEntity = null;
            try{
                productsEntity = productRepository.findById(reviewItemRequest.getProductId()).get();
            }catch (NoSuchElementException ex){
                messageDTO.setMessage("Product Not Found");
                messageDTO.setStatus(HttpStatus.NOT_FOUND);
                return messageDTO;
            }

            try {
                userEntity = userRepository.findById(reviewItemRequest.getUserId()).get();
            }catch (NoSuchElementException ex){
                messageDTO.setMessage("User Not Found");
                messageDTO.setStatus(HttpStatus.NOT_FOUND);
                return messageDTO;
            }
            ReviewEntity reviewEntity = new ReviewEntity();
            reviewEntity.setUserEntity(userEntity);
            reviewEntity.setProductsEntity(productsEntity);
            reviewEntity.setCreatedAt(LocalDateTime.now());
            reviewEntity.setStar(reviewItemRequest.getStar());
            reviewEntity.setComment(reviewItemRequest.getComment());
            reviewRepository.save(reviewEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Review Added");
        }
        return messageDTO;
    }

    @Override
    public MessageDTO deleteReview(Long idUser, Long idProduct) {
        MessageDTO messageDTO = new MessageDTO();
        ProductsEntity productsEntity = null;
        UserEntity userEntity = null;
        try{
            productsEntity = productRepository.findById(idProduct).get();
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Product Not Found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }

        try {
            userEntity = userRepository.findById(idUser).get();
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("User Not Found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        ReviewEntity reviewEntity = reviewRepository.findByUserEntityAndProductsEntity(userEntity, productsEntity);
        reviewRepository.delete(reviewEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Review Deleted");
        return messageDTO;
    }
}
