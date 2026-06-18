package org.example.petshop.DTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SizeDTO {
    private Long idSize;
    private String size;
}
