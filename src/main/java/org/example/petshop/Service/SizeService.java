package org.example.petshop.Service;

import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.SizeDTO;

public interface SizeService {
    DataResponse getAllSize();
    Object getSizeById(Long idSize);
    MessageDTO addSize(SizeDTO sizeDTO);
    MessageDTO updateSize(SizeDTO sizeDTO);
    MessageDTO deleteSize(Long idSize);
}
