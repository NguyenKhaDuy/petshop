package org.example.petshop.Service;

import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.ReviewProductDTO;
import org.example.petshop.DTO.ReviewRequest;
import org.springframework.data.domain.Page;

public interface ReviewService {
    Page<ReviewProductDTO> getReviews(Integer page);
    Object getReviewByUser(Long idUser);
    MessageDTO addReview(ReviewRequest reviewRequest);
    MessageDTO deleteReview(Long idUser, Long idProduct);
    MessageDTO deleteReviewById(Long idReview, Long idUser);
}
