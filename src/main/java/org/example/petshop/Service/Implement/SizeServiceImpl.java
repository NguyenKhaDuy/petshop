package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.SizeDTO;
import org.example.petshop.Entity.SizeEntity;
import org.example.petshop.Repository.SizeRepository;
import org.example.petshop.Service.SizeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SizeServiceImpl implements SizeService {
    @Autowired
    SizeRepository sizeRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public DataResponse getAllSize() {
        DataResponse dataResponse = new DataResponse();
        List<SizeEntity> sizeEntities = sizeRepository.findAll();
        List<SizeDTO> sizeDTOS = new ArrayList<>();
        for (SizeEntity sizeEntity : sizeEntities) {
            SizeDTO sizeDTO = new SizeDTO();
            modelMapper.map(sizeEntity, sizeDTO);
            sizeDTOS.add(sizeDTO);
        }
        dataResponse.setData(sizeDTOS);
        dataResponse.setMessage("Success");
        dataResponse.setStatus(HttpStatus.OK);
        return dataResponse;
    }

    @Override
    public Object getSizeById(Long idSize) {
        MessageDTO messageDTO = new MessageDTO();
        DataResponse dataResponse = new DataResponse();
        try {
            SizeEntity sizeEntity = sizeRepository.findById(idSize).get();
            SizeDTO sizeDTO = new SizeDTO();
            modelMapper.map(sizeEntity, sizeDTO);
            dataResponse.setData(sizeDTO);
            dataResponse.setMessage("Success");
            dataResponse.setStatus(HttpStatus.OK);
            return dataResponse;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("Size not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO addSize(SizeDTO sizeDTO) {
        MessageDTO messageDTO = new MessageDTO();
        SizeEntity sizeEntity = new SizeEntity();
        modelMapper.map(sizeEntity, sizeDTO);
        sizeRepository.save(sizeEntity);
        messageDTO.setStatus(HttpStatus.OK);
        messageDTO.setMessage("Success");
        return messageDTO;
    }

    @Override
    public MessageDTO updateSize(SizeDTO sizeDTO) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            SizeEntity sizeEntity = sizeRepository.findById(sizeDTO.getIdSize()).get();
            modelMapper.map(sizeDTO, sizeEntity);
            sizeRepository.save(sizeEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("size not found");
            return messageDTO;
        }
    }

    @Override
    public MessageDTO deleteSize(Long idSize) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            SizeEntity sizeEntity = sizeRepository.findById(idSize).get();
            sizeRepository.delete(sizeEntity);
            messageDTO.setStatus(HttpStatus.OK);
            messageDTO.setMessage("Success");
            return messageDTO;
        }catch (NoSuchElementException e){
            messageDTO.setStatus(HttpStatus.NOT_FOUND);
            messageDTO.setMessage("size not found");
            return messageDTO;
        }
    }
}
