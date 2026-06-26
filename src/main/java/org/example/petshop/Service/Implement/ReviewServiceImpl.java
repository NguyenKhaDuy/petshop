package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.ReviewItemRequest;
import org.example.petshop.DTO.ReviewProductDTO;
import org.example.petshop.DTO.ReviewRequest;
import org.example.petshop.Entity.ProductImageEntity;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.ReviewEntity;
import org.example.petshop.Entity.UserEntity;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.ReviewRepository;
import org.example.petshop.Repository.UserRepository;
import org.example.petshop.Service.ReviewService;
import org.example.petshop.Utils.ConvertByteToBase64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewProductDTO> getReviews(Integer page) {
        int currentPage = Math.max(page == null ? 1 : page, 1);
        Pageable pageable = PageRequest.of(currentPage - 1, 20);
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toReviewProductDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getReviewByUser(Long idUser) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try {
            UserEntity userEntity = userRepository.findById(idUser).get();
            List<ReviewProductDTO> reviewProductDTOS = new ArrayList<>();
            for (ReviewEntity reviewEntity : reviewRepository.findByUserEntityOrderByCreatedAtDesc(userEntity)) {
                reviewProductDTOS.add(toReviewProductDTO(reviewEntity));
            }
            dataResponse.setData(reviewProductDTOS);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        } catch (NoSuchElementException ex) {
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("User Not Found");
            return messageDTO;
        }
    }

    @Override
    @Transactional
    public MessageDTO addReview(ReviewRequest reviewRequest) {
        MessageDTO messageDTO = new MessageDTO();
        if (reviewRequest == null
                || reviewRequest.getReviewItemRequests() == null
                || reviewRequest.getReviewItemRequests().isEmpty()) {
            messageDTO.setStatus(HttpStatus.BAD_REQUEST);
            messageDTO.setMessage("Not null");
            return messageDTO;
        }

        for (ReviewItemRequest reviewItemRequest : reviewRequest.getReviewItemRequests()) {
            if (reviewItemRequest == null
                    || reviewItemRequest.getUserId() == null
                    || reviewItemRequest.getProductId() == null) {
                messageDTO.setStatus(HttpStatus.BAD_REQUEST);
                messageDTO.setMessage("Complete");
                return messageDTO;
            }
            if (reviewItemRequest.getStar() == null
                    || reviewItemRequest.getStar() < 1
                    || reviewItemRequest.getStar() > 5) {
                messageDTO.setStatus(HttpStatus.BAD_REQUEST);
                messageDTO.setMessage("from 1 to 5");
                return messageDTO;
            }

            ProductsEntity productsEntity;
            UserEntity userEntity;
            try {
                productsEntity = productRepository.findById(reviewItemRequest.getProductId()).get();
            } catch (NoSuchElementException ex) {
                messageDTO.setMessage("Product Not Found");
                messageDTO.setStatus(HttpStatus.NOT_FOUND);
                return messageDTO;
            }

            try {
                userEntity = userRepository.findById(reviewItemRequest.getUserId()).get();
            } catch (NoSuchElementException ex) {
                messageDTO.setMessage("User Not Found");
                messageDTO.setStatus(HttpStatus.NOT_FOUND);
                return messageDTO;
            }
            if (isAdmin(userEntity)) {
                return adminCanOnlyViewReviewsMessage();
            }

            List<ReviewEntity> existingReviews = reviewRepository.findByUserEntityAndProductsEntity(userEntity, productsEntity);
            ReviewEntity reviewEntity = existingReviews.isEmpty() ? new ReviewEntity() : existingReviews.get(0);
            reviewEntity.setUserEntity(userEntity);
            reviewEntity.setProductsEntity(productsEntity);
            reviewEntity.setCreatedAt(LocalDateTime.now());
            reviewEntity.setStar(reviewItemRequest.getStar());
            reviewEntity.setComment(reviewItemRequest.getComment());
            reviewRepository.save(reviewEntity);

            for (int i = 1; i < existingReviews.size(); i++) {
                reviewRepository.delete(existingReviews.get(i));
            }
        }

        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Review Saved");
        return messageDTO;
    }

    @Override
    @Transactional
    public MessageDTO deleteReview(Long idUser, Long idProduct) {
        MessageDTO messageDTO = new MessageDTO();
        ProductsEntity productsEntity;
        UserEntity userEntity;
        try {
            productsEntity = productRepository.findById(idProduct).get();
        } catch (NoSuchElementException ex) {
            messageDTO.setMessage("Product Not Found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }

        try {
            userEntity = userRepository.findById(idUser).get();
        } catch (NoSuchElementException ex) {
            messageDTO.setMessage("User Not Found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        if (isAdmin(userEntity)) {
            return adminCanOnlyViewReviewsMessage();
        }

        List<ReviewEntity> reviewEntities = reviewRepository.findByUserEntityAndProductsEntity(userEntity, productsEntity);
        if (reviewEntities.isEmpty()) {
            messageDTO.setMessage("Review Not Found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        reviewRepository.deleteAll(reviewEntities);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Review Deleted");
        return messageDTO;
    }

    @Override
    @Transactional
    public MessageDTO deleteReviewById(Long idReview, Long idUser) {
        MessageDTO messageDTO = new MessageDTO();
        if (idReview == null || idUser == null) {
            messageDTO.setStatus(HttpStatus.BAD_REQUEST);
            messageDTO.setMessage("Complete");
            return messageDTO;
        }

        try {
            ReviewEntity reviewEntity = reviewRepository.findById(idReview).get();
            Long ownerId = reviewEntity.getUserEntity() != null
                    ? reviewEntity.getUserEntity().getIdUser()
                    : null;
            if (isAdmin(reviewEntity.getUserEntity())) {
                return adminCanOnlyViewReviewsMessage();
            }
            if (!Objects.equals(ownerId, idUser)) {
                messageDTO.setStatus(HttpStatus.FORBIDDEN);
                messageDTO.setMessage("You can only delete your own review");
                return messageDTO;
            }
            reviewRepository.delete(reviewEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Review Deleted");
            return messageDTO;
        } catch (NoSuchElementException ex) {
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Review Not Found");
            return messageDTO;
        }
    }

    private ReviewProductDTO toReviewProductDTO(ReviewEntity reviewEntity) {
        ReviewProductDTO reviewProductDTO = new ReviewProductDTO();
        ProductsEntity product = reviewEntity.getProductsEntity();
        UserEntity user = reviewEntity.getUserEntity();
        reviewProductDTO.setIdReview(reviewEntity.getIdReview());
        reviewProductDTO.setStar(reviewEntity.getStar());
        reviewProductDTO.setCreatedAt(reviewEntity.getCreatedAt());
        reviewProductDTO.setComment(reviewEntity.getComment());
        if (user != null) {
            reviewProductDTO.setIdUser(user.getIdUser());
            reviewProductDTO.setNameUser(user.getName());
        }
        if (product != null) {
            reviewProductDTO.setIdProduct(product.getIdProduct());
            reviewProductDTO.setNameProduct(product.getNameProduct());
            reviewProductDTO.setImageProduct(getFirstProductImage(product));
        }
        return reviewProductDTO;
    }

    private boolean isAdmin(UserEntity userEntity) {
        return userEntity != null && "ADMIN".equalsIgnoreCase(userEntity.getRole());
    }

    private MessageDTO adminCanOnlyViewReviewsMessage() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setStatus(HttpStatus.FORBIDDEN);
        messageDTO.setMessage("Admin cant delete");
        return messageDTO;
    }

    private String getFirstProductImage(ProductsEntity product) {
        if (product == null
                || product.getProductImageEntities() == null
                || product.getProductImageEntities().isEmpty()) {
            return null;
        }
        ProductImageEntity image = product.getProductImageEntities().get(0);
        if (image == null || image.getImage() == null) {
            return null;
        }
        return ConvertByteToBase64.toBase64(image.getImage());
    }
}
