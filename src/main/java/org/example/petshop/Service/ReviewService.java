package org.example.petshop.Service;

import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.ReviewRequest;

import java.util.List;

public interface ReviewService {
    MessageDTO addReview(ReviewRequest reviewRequest);
    MessageDTO deleteReview(Long idUser, Long idProduct);
}
