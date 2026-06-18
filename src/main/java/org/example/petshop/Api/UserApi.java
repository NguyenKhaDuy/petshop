package org.example.petshop.Api;

import org.example.petshop.DTO.*;
import org.example.petshop.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserApi {
    @Autowired
    UserService userService;

    @PostMapping(value = "/api/login")
    public ResponseEntity<Object> login(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
        Object result = userService.login(email, password);
        if (result instanceof MessageDTO) {
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/register")
    public ResponseEntity<Object> register(@RequestBody RegisterDTO registerDTO) {
        MessageDTO result = userService.register(registerDTO);
        return new ResponseEntity<>(result, result.getStatus());
    }

    @GetMapping(value = "/api/admin/user")
    public ResponseEntity<Object> getUsers(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<UserDTO> userDTOS = userService.getUsers(page);
        DataPageResponse dataPageResponse = new DataPageResponse();
        dataPageResponse.setData(userDTOS.getContent());
        dataPageResponse.setCurrentPage(page);
        dataPageResponse.setMessage("Success");
        dataPageResponse.setTotalPages(userDTOS.getTotalPages());
        return new ResponseEntity<>(dataPageResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/user/id-user={id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        Object result = userService.getUserById(id);
        if (result instanceof MessageDTO) {
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/information-order")
    public ResponseEntity<Object> addInformationOrder(@RequestBody AddInformarionOrder addInformarionOrder) {
        MessageDTO messageDTO = userService.addInformationOrder(addInformarionOrder);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/information-order/id={id}")
    public ResponseEntity<Object> deleteInformationOrder(@PathVariable Long id) {
        MessageDTO messageDTO = userService.deleteInformationOrder(id);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/api/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePassword changePassword){
        MessageDTO messageDTO = userService.changePassword(changePassword);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }
}
