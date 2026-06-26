package org.example.petshop.Api;

import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.DataPageResponse;
import org.example.petshop.DTO.ReviewProductDTO;
import org.example.petshop.DTO.ReviewRequest;
import org.example.petshop.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewApi {
    @Autowired
    ReviewService reviewService;

    @GetMapping(value = "/api/admin/reviews")
    public ResponseEntity<Object> getAllReviews(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<ReviewProductDTO> reviewProductDTOS = reviewService.getReviews(page);
        DataPageResponse dataPageResponse = new DataPageResponse();
        dataPageResponse.setData(reviewProductDTOS.getContent());
        dataPageResponse.setCurrentPage(page);
        dataPageResponse.setTotalPages(reviewProductDTOS.getTotalPages());
        dataPageResponse.setStatus(HttpStatus.OK);
        dataPageResponse.setMessage("Success");
        return new ResponseEntity<>(dataPageResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/user/review/id-user={id}")
    public ResponseEntity<Object> getReviewByUser(@PathVariable("id") Long id) {
        Object result = reviewService.getReviewByUser(id);
        if (result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/user/review")
    public ResponseEntity<Object> addReview(@RequestBody ReviewRequest reviewRequest){
        MessageDTO messageDTO = reviewService.addReview(reviewRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @DeleteMapping(value = "/api/user/review/id-review={idReview}")
    public ResponseEntity<Object> deleteReviewById(@PathVariable("idReview") Long idReview,
                                                   @RequestParam(name = "idUser") Long idUser){
        MessageDTO messageDTO = reviewService.deleteReviewById(idReview, idUser);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @DeleteMapping(value = "/api/user/review")
    public ResponseEntity<Object> deleteReview(@RequestParam(name = "idUser") Long idUser,
                                               @RequestParam(name = "idProduct") Long idProduct){
        MessageDTO messageDTO = reviewService.deleteReview(idUser, idProduct);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }
}
