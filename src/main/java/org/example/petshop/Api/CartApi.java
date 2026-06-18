package org.example.petshop.Api;

import org.example.petshop.DTO.AddProductToCart;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartApi {
    @Autowired
    CartService cartService;

    @GetMapping(value = "/api/cart/id-user={id}")
    public ResponseEntity<Object> getCartByUser(@PathVariable Long id){
        Object result = cartService.getCartByUser(id);
        if(result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/cart/item")
    public ResponseEntity<Object> addCart(@RequestBody AddProductToCart addProductToCart){
        MessageDTO messageDTO = cartService.addProductToCart(addProductToCart);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }

    @DeleteMapping(value = "/api/cart/id-cart-item={id}")
    public ResponseEntity<Object> deleteCart(@PathVariable Long id){
        MessageDTO messageDTO = cartService.removeProductFromCart(id);
        return new ResponseEntity<>(messageDTO, messageDTO.getStatus());
    }
}
