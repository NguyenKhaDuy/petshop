package org.example.petshop.Api;

import org.example.petshop.DTO.CategoryRequest;
import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.SizeDTO;
import org.example.petshop.Service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SizeApi {
    @Autowired
    SizeService sizeService;

    @GetMapping(value = "/api/admin/size")
    public ResponseEntity<Object> getSize() {
        DataResponse dataResponse = sizeService.getAllSize();
        return new ResponseEntity<>(dataResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/admin/size/id-size={id}")
    public ResponseEntity<Object> getSizeById(@PathVariable("id") Long id) {
        Object result = sizeService.getSizeById(id);
        if (result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/admin/size")
    public ResponseEntity<Object> addSize(@RequestBody SizeDTO sizeDTO) {
        MessageDTO messageDTO = sizeService.addSize(sizeDTO);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/api/admin/size")
    public ResponseEntity<Object> updateSize(@RequestBody SizeDTO sizeDTO) {
        MessageDTO messageDTO = sizeService.updateSize(sizeDTO);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/admin/size/id-size={id}")
    public ResponseEntity<Object> deleteSize(@PathVariable("id") Long id) {
        MessageDTO messageDTO = sizeService.deleteSize(id);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }
}
