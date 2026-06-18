package org.example.petshop.Service;

import org.example.petshop.DTO.AddProductToCart;
import org.example.petshop.DTO.MessageDTO;

public interface CartService {
    Object getCartByUser(Long idUser);
    MessageDTO addProductToCart(AddProductToCart addProductToCart);
    MessageDTO removeProductFromCart(Long idCartItem);
}
