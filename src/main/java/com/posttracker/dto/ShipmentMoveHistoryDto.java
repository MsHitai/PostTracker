package com.posttracker.dto;

import com.posttracker.model.Type;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ShipmentMoveHistoryDto {
    private Long id;
    @NotNull(message = "Необходимо указать тип посылки")
    private Type type;
    @NotNull(message = "Необходимо указать индекс получателя")
    private int recipientIndex;
    @NotNull(message = "Необходимо указать адрес получателя")
    private String recipientAddress;
    @NotNull(message = "Необходимо указать имя получателя")
    private String recipientName;
    private List<ShipmentMovementDto> movements;
}
