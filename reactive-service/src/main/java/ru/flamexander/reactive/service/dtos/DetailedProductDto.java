package ru.flamexander.reactive.service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedProductDto {
    private Long id;
    private String name;
    private String description;
}
