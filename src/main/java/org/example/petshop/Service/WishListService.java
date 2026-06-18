package org.example.petshop.Service;

import org.example.petshop.DTO.AddWishListRequest;
import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;

public interface WishListService {
    Object getAllWishListByUser(Long idUser);
    MessageDTO addWishList(AddWishListRequest addWishListRequest);
    MessageDTO deleteWishList(Long idWishList);
}
