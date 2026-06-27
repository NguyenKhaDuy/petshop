package org.example.petshop;

import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.ReviewItemRequest;
import org.example.petshop.DTO.ReviewRequest;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.ReviewEntity;
import org.example.petshop.Entity.UserEntity;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.ReviewRepository;
import org.example.petshop.Repository.UserRepository;
import org.example.petshop.Service.Implement.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceUnitTests {
    @Mock
    ProductRepository productRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ReviewRepository reviewRepository;
    @InjectMocks
    ReviewServiceImpl reviewService;

    @Test
    void addReviewUpdatesExistingReviewForSameUserAndProduct() {
        ProductsEntity product = new ProductsEntity();
        product.setIdProduct(10L);
        UserEntity user = new UserEntity();
        user.setIdUser(20L);
        ReviewEntity existingReview = new ReviewEntity();
        existingReview.setIdReview(30L);
        existingReview.setUserEntity(user);
        existingReview.setProductsEntity(product);
        existingReview.setStar(2);
        existingReview.setComment("Old comment");

        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserEntityAndProductsEntity(user, product))
                .thenReturn(List.of(existingReview));

        ReviewRequest request = new ReviewRequest(List.of(
                new ReviewItemRequest(5, "Sản phẩm rất ổn", 20L, 10L)
        ));

        MessageDTO result = reviewService.addReview(request);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(5, existingReview.getStar());
        assertEquals("Sản phẩm rất ổn", existingReview.getComment());
        verify(reviewRepository).save(existingReview);
    }

    @Test
    void deleteReviewByIdDeletesOnlyWhenUserOwnsReview() {
        UserEntity owner = new UserEntity();
        owner.setIdUser(20L);
        ReviewEntity review = new ReviewEntity();
        review.setIdReview(30L);
        review.setUserEntity(owner);

        when(reviewRepository.findById(30L)).thenReturn(Optional.of(review));

        MessageDTO result = reviewService.deleteReviewById(30L, 20L);

        assertEquals(HttpStatus.OK, result.getStatus());
        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReviewByIdRejectsReviewOwnedByAnotherUser() {
        UserEntity owner = new UserEntity();
        owner.setIdUser(99L);
        ReviewEntity review = new ReviewEntity();
        review.setIdReview(30L);
        review.setUserEntity(owner);

        when(reviewRepository.findById(30L)).thenReturn(Optional.of(review));

        MessageDTO result = reviewService.deleteReviewById(30L, 20L);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
        verify(reviewRepository, never()).delete(review);
    }

    @Test
    void deleteReviewByIdRejectsAdminReviewActions() {
        UserEntity admin = new UserEntity();
        admin.setIdUser(20L);
        admin.setRole("ADMIN");
        ReviewEntity review = new ReviewEntity();
        review.setIdReview(30L);
        review.setUserEntity(admin);

        when(reviewRepository.findById(30L)).thenReturn(Optional.of(review));

        MessageDTO result = reviewService.deleteReviewById(30L, 20L);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
        verify(reviewRepository, never()).delete(review);
    }
}
