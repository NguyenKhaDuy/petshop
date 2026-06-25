package org.example.petshop.Service.Implement;

import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.PaymentMethodDTO;
import org.example.petshop.Repository.PaymentMethodRepository;
import org.example.petshop.Service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Override
    public DataResponse getPaymentMethods() {
        List<PaymentMethodDTO> paymentMethods = paymentMethodRepository.findAll()
                .stream()
                .map(entity -> new PaymentMethodDTO(entity.getIdmethod(), entity.getMethod()))
                .toList();

        DataResponse<List<PaymentMethodDTO>> response = new DataResponse<>();
        response.setData(paymentMethods);
        response.setMessage("Success");
        response.setStatus(HttpStatus.OK);
        return response;
    }
}
