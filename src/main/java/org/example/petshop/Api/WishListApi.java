package org.example.petshop.Api;

import lombok.Getter;
import org.example.petshop.DTO.AddWishListRequest;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.WishListDTO;
import org.example.petshop.Service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WishListApi {
    @Autowired
    WishListService wishListService;

    @GetMapping(value = "/api/wishlist/id-user={id}")
    public ResponseEntity<Object> getAllWishListByUser(@PathVariable Long id) {
        Object result = wishListService.getAllWishListByUser(id);
        if (result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/wishlist")
    public ResponseEntity<Object> addWishList(@RequestBody AddWishListRequest addWishListRequest) {
        MessageDTO result = wishListService.addWishList(addWishListRequest);
        return new ResponseEntity<>(result, result.getStatus());
    }

    @DeleteMapping(value = "/api/wishlist/id-wishlist={id}")
    public ResponseEntity<Object> deleteWishList(@PathVariable Long id) {
        MessageDTO result = wishListService.deleteWishList(id);
        return new ResponseEntity<>(result, result.getStatus());
    }
}
