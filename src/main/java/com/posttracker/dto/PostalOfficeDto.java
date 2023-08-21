package com.posttracker.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostalOfficeDto {
    private Long id;
    @NotNull(message = "Необходимо указать индекс почтового отделения")
    private int index;
    @NotNull(message = "Необходимо указать название почтового отделения")
    private String name;
    @NotNull(message = "Необходимо указать адрес получателя")
    private String recipientAddress;
}
