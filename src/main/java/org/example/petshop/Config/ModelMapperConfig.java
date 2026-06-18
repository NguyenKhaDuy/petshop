package org.example.petshop.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper ModalMapperConfig(){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}
