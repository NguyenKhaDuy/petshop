package org.example.petshop.Service;

import org.example.petshop.DTO.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VoucherService {
    DataResponse getAllVouchers();
    Page<VoucherDTO> getAllVouchers(Integer page);
    Object getVoucherById(Long idVoucher);
    MessageDTO addvoucher(VoucherRequest voucherRequest);
    MessageDTO updateVoucher(VoucherRequest voucherRequest);
    MessageDTO deleteVoucher(Long idVoucher);
}
