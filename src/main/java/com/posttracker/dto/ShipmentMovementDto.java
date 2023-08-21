package com.posttracker.dto;

import com.posttracker.model.Type;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ShipmentMovementDto {
    private Long id;
    @Enumerated(EnumType.STRING)
    private Type shipmentType;
    @Column(nullable = false)
    private int postalIndex;

    private LocalDateTime arrived;

    private LocalDateTime departed;
}
