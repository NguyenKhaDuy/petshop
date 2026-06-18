package org.example.petshop.Service;

import org.example.petshop.DTO.*;
import org.springframework.data.domain.Page;

public interface UserService {
    MessageDTO register(RegisterDTO registerDTO);
    Object login(String email, String password);
    Page<UserDTO> getUsers(Integer page);
    Object getUserById(Long idUser);
    MessageDTO addInformationOrder(AddInformarionOrder addInformarionOrder);
    MessageDTO deleteInformationOrder(Long idInformationOrder);
    MessageDTO changePassword(ChangePassword changePassword);
}
