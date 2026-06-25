package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.*;
import org.example.petshop.Entity.VoucherEntity;
import org.example.petshop.Repository.VoucherRepository;
import org.example.petshop.Service.VoucherService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VoucherServiceImpl implements VoucherService {
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    ModelMapper modelMapper;


    @Override
    public DataResponse getAvailableVouchers() {
        DataResponse dataResponse = new DataResponse();
        List<VoucherEntity> voucherEntities = voucherRepository.findAll();
        List<VoucherDTO> voucherDTOS = new ArrayList<>();
        for (VoucherEntity voucherEntity : voucherEntities) {
            boolean available = voucherEntity.getQuantity() != null
                    && voucherEntity.getQuantity() > 0
                    && voucherEntity.getExpiredDate() != null
                    && !voucherEntity.getExpiredDate().isBefore(LocalDate.now());
            if (!available) {
                continue;
            }
            VoucherDTO voucherDTO = new VoucherDTO();
            modelMapper.map(voucherEntity, voucherDTO);
            voucherDTOS.add(voucherDTO);
        }
        dataResponse.setData(voucherDTOS);
        dataResponse.setStatus(HttpStatus.OK);
        dataResponse.setMessage("Success");
        return dataResponse;
    }

    @Override
    public Page<VoucherDTO> getAllVouchers(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 20);
        Page<VoucherEntity> voucherEntities = voucherRepository.findAll(pageable);
        List<VoucherDTO> voucherDTOS = new ArrayList<>();
        for (VoucherEntity voucherEntity : voucherEntities) {
            VoucherDTO voucherDTO = new VoucherDTO();
            modelMapper.map(voucherEntity, voucherDTO);
            voucherDTOS.add(voucherDTO);
        }
        return new PageImpl<>(voucherDTOS, voucherEntities.getPageable(), voucherEntities.getTotalElements());
    }

    @Override
    public Object getVoucherById(Long idVoucher) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try {
            VoucherEntity voucherEntity = voucherRepository.findById(idVoucher).get();
            VoucherDTO voucherDTO = new VoucherDTO();
            modelMapper.map(voucherEntity, voucherDTO);
            dataResponse.setData(voucherDTO);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("voucher not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addvoucher(VoucherRequest voucherRequest) {
        MessageDTO messageDTO = new MessageDTO();
        VoucherEntity voucherEntity = new VoucherEntity();
        modelMapper.map(voucherRequest, voucherEntity);
        voucherEntity.setCreatedAt(LocalDateTime.now());
        voucherRepository.save(voucherEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO updateVoucher(VoucherRequest voucherRequest) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            VoucherEntity voucherEntity = voucherRepository.findById(voucherRequest.getIdVoucher()).get();
            modelMapper.map(voucherRequest, voucherEntity);
            voucherRepository.save(voucherEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("voucher not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO deleteVoucher(Long idVoucher) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            VoucherEntity voucherEntity = voucherRepository.findById(idVoucher).get();
            voucherRepository.delete(voucherEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("voucher not found");
            return messageDTO;
        }
    }
}
