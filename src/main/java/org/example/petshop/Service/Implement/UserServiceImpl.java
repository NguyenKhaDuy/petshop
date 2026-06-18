package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.*;
import org.example.petshop.Entity.InformationOrderEntity;
import org.example.petshop.Entity.UserEntity;
import org.example.petshop.Repository.InformationOrderRepository;
import org.example.petshop.Repository.UserRepository;
import org.example.petshop.Service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    InformationOrderRepository informationOrderRepository;

    @Override
    public MessageDTO register(RegisterDTO registerDTO) {
        UserEntity userEntity = userRepository.findByEmail(registerDTO.getEmail());
        MessageDTO messageDTO = new MessageDTO();
        List<InformationOrderEntity> informationOrderEntities = new ArrayList<>();
        if (userEntity != null) {
            messageDTO.setMessage("User already exists");
            messageDTO.setStatus(HttpStatus.BAD_GATEWAY);
        }
        UserEntity user = new UserEntity();
        modelMapper.map(registerDTO, user);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        InformationOrderEntity informationOrderEntity = new InformationOrderEntity();
        informationOrderEntity.setAddressOrder(registerDTO.getAddressOrder());
        informationOrderEntity.setPhoneOrder(registerDTO.getPhoneOrder());
        informationOrderEntity.setNameOrder(registerDTO.getName());
        informationOrderEntity.setUserEntity(user);
        informationOrderEntities.add(informationOrderEntity);
        user.setInformationOrderEntities(informationOrderEntities);
        user.setRole("CUSTOMER");
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("User registered successfully");
        return messageDTO;
    }

    @Override
    public Object login(String email, String password) {
        MessageDTO messageDTO = new MessageDTO();
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            messageDTO.setMessage("User not exists");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail(userEntity.getEmail());
            loginDTO.setIdUser(userEntity.getIdUser());
            loginDTO.setMessage("Successfully logged in");
            loginDTO.setStatus(HttpStatus.OK);
            return loginDTO;
        }else {
            messageDTO.setMessage("Incorrect password");
            messageDTO.setStatus(HttpStatus.BAD_REQUEST);
            return messageDTO;
        }
    }

    @Override
    public Page<UserDTO> getUsers(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        Page<UserEntity> userEntities = userRepository.findAll(pageable);
        List<UserDTO> userDTOs = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserDTO userDTO = new UserDTO();
            modelMapper.map(userEntity, userDTO);
            List<InformationOrderDTO> informationOrderDTOS = new ArrayList<>();
            for (InformationOrderEntity informationOrderEntity : userEntity.getInformationOrderEntities()) {
                InformationOrderDTO informationOrderDTO = new InformationOrderDTO();
                modelMapper.map(informationOrderEntity, informationOrderDTO);
                informationOrderDTOS.add(informationOrderDTO);
            }
            userDTO.setInformationOrderDTOS(informationOrderDTOS);
            userDTOs.add(userDTO);
        }
        return new PageImpl<>(userDTOs, userEntities.getPageable(), userEntities.getTotalElements());
    }

    @Override
    public Object getUserById(Long idUser) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try{
            UserEntity userEntity = userRepository.findById(idUser).get();
            UserDTO userDTO = new UserDTO();
            modelMapper.map(userEntity, userDTO);
            List<InformationOrderDTO> informationOrderDTOS = new ArrayList<>();
            for (InformationOrderEntity informationOrderEntity : userEntity.getInformationOrderEntities()) {
                InformationOrderDTO informationOrderDTO = new InformationOrderDTO();
                modelMapper.map(informationOrderEntity, informationOrderDTO);
                informationOrderDTOS.add(informationOrderDTO);
            }
            userDTO.setInformationOrderDTOS(informationOrderDTOS);
            dataResponse.setData(userDTO);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("User not found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addInformationOrder(AddInformarionOrder addInformarionOrder) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            UserEntity userEntity = userRepository.findById(addInformarionOrder.getIdUser()).get();
            InformationOrderEntity informationOrderEntity = new InformationOrderEntity();
            informationOrderEntity.setUserEntity(userEntity);
            informationOrderEntity.setAddressOrder(addInformarionOrder.getAddressOrder());
            informationOrderEntity.setPhoneOrder(addInformarionOrder.getPhoneOrder());
            informationOrderEntity.setNameOrder(addInformarionOrder.getNameOrder());
            userEntity.getInformationOrderEntities().add(informationOrderEntity);
            userRepository.save(userEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("User not found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
    }

    @Override
    public MessageDTO deleteInformationOrder(Long idInformationOrder) {
        MessageDTO messageDTO = new MessageDTO();
        try{
            InformationOrderEntity informationOrder = informationOrderRepository.findById(idInformationOrder).get();
            informationOrderRepository.delete(informationOrder);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("Information order not found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
    }

    @Override
    public MessageDTO changePassword(ChangePassword changePassword) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            UserEntity userEntity = userRepository.findById(changePassword.getIdUser()).get();
            if (passwordEncoder.matches(changePassword.getOldPassword(), userEntity.getPassword())) {
                userEntity.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
                userRepository.save(userEntity);
                messageDTO.setStatus(HttpStatus.OK);
                messageDTO.setMessage("Success");
            }else {
                messageDTO.setMessage("Incorrect password");
                messageDTO.setStatus(HttpStatus.BAD_REQUEST);
            }
            return messageDTO;
        }catch (NoSuchElementException ex){
            messageDTO.setMessage("User not found");
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            return messageDTO;
        }
    }
}
