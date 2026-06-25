package org.example.petshop.Api;

import org.example.petshop.DTO.DataResponse;
import org.example.petshop.Service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentMethodApi {
    @Autowired
    PaymentMethodService paymentMethodService;

    @GetMapping(value = "/api/payment-method")
    public ResponseEntity<Object> getPaymentMethods() {
        DataResponse response = paymentMethodService.getPaymentMethods();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
