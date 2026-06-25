package org.example.petshop.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewItemRequest {
    private Integer star;
    private String comment;
    private Long userId;
    private Long productId;
}
