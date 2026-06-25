package org.example.petshop.Api;

import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.ReviewRequest;
import org.example.petshop.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewApi {
    @Autowired
    ReviewService reviewService;

    @PostMapping(value = "/api/user/review")
    public ResponseEntity<Object> addReview(@RequestBody ReviewRequest reviewRequest){
        MessageDTO messageDTO = reviewService.addReview(reviewRequest);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @DeleteMapping(value = "/api/user/review")
    public ResponseEntity<Object> deleteReview(@RequestParam(name = "idUser") Long idUser,
                                               @RequestParam(name = "idProduct") Long idProduct){
        MessageDTO messageDTO = reviewService.deleteReview(idUser, idProduct);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }
}
