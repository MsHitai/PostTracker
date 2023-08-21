package com.posttracker.mapper;

import com.posttracker.dto.ShipmentDto;
import com.posttracker.model.Shipment;
import lombok.Generated;
import lombok.experimental.UtilityClass;

@UtilityClass
@Generated
public class ShipmentMapper {

    public Shipment mapToShipment(ShipmentDto shipmentDto) {
        return Shipment.builder()
                .id(shipmentDto.getId())
                .type(shipmentDto.getType())
                .recipientAddress(shipmentDto.getRecipientAddress())
                .recipientName(shipmentDto.getRecipientName())
                .recipientIndex(shipmentDto.getRecipientIndex())
                .received(shipmentDto.getReceived())
                .build();
    }

    public ShipmentDto mapToDto(Shipment shipment) {
        return new ShipmentDto(
                shipment.getId(),
                shipment.getType(),
                shipment.getRecipientIndex(),
                shipment.getRecipientAddress(),
                shipment.getRecipientName(),
                shipment.getReceived()
        );
    }
}
