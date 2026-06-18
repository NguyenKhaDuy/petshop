package org.example.petshop.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataPageResponse <T>{
    private Integer currentPage;
    private Integer totalPages;
    private String message;
    private HttpStatus status;
    private T data;
}
