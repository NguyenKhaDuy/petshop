package org.example.petshop.Api;

import org.example.petshop.DTO.*;
import org.example.petshop.Service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VoucherApi {
    @Autowired
    VoucherService voucherService;

    @GetMapping(value = "/api/admin/voucher")
    public ResponseEntity<Object> getVouchers(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        Page<VoucherDTO> voucherDTOS = voucherService.getAllVouchers(page);
        DataPageResponse dataPageResponse = new DataPageResponse();
        dataPageResponse.setData(voucherDTOS.getContent());
        dataPageResponse.setCurrentPage(page);
        dataPageResponse.setMessage("Success");
        dataPageResponse.setTotalPages(voucherDTOS.getTotalPages());
        return new ResponseEntity<>(dataPageResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/voucher")
    public ResponseEntity<Object> getVouchers() {
        DataResponse dataResponse = voucherService.getAvailableVouchers();
        return new ResponseEntity<>(dataResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/api/admin/voucher/id-voucher={id}")
    public ResponseEntity<Object> getVoucherById(@PathVariable("id") Long id) {
        Object result = voucherService.getVoucherById(id);
        if (result instanceof MessageDTO){
            return new ResponseEntity<>(result, ((MessageDTO) result).getStatus());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/admin/voucher")
    public ResponseEntity<Object> addVoucher(@RequestBody VoucherRequest voucherRequest) {
        MessageDTO messageDTO = voucherService.addvoucher(voucherRequest);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/api/admin/voucher")
    public ResponseEntity<Object> updateVoucher(@RequestBody VoucherRequest voucherRequest) {
        MessageDTO messageDTO = voucherService.updateVoucher(voucherRequest);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }

    @DeleteMapping(value = "/api/admin/voucher/id-voucher={id}")
    public ResponseEntity<Object> deleteVoucher(@PathVariable("id") Long id) {
        MessageDTO messageDTO = voucherService.deleteVoucher(id);
        return new ResponseEntity<>(messageDTO, HttpStatus.OK);
    }
}
