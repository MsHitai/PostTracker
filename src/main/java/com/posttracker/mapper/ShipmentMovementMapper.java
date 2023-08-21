package com.posttracker.mapper;

import com.posttracker.dto.ShipmentMoveHistoryDto;
import com.posttracker.dto.ShipmentMovementDto;
import com.posttracker.model.Shipment;
import com.posttracker.model.ShipmentMovement;
import lombok.Generated;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
@Generated
public class ShipmentMovementMapper {

    public ShipmentMovement mapToShipmentMovement(ShipmentMovementDto dto, Shipment shipment) {
        return ShipmentMovement.builder()
                .id(dto.getId())
                .postalIndex(dto.getPostalIndex())
                .shipmentType(dto.getShipmentType())
                .arrived(dto.getArrived())
                .shipment(shipment)
                .build();
    }

    public ShipmentMovementDto mapToDto(ShipmentMovement shipmentMovement) {
        return ShipmentMovementDto.builder()
                .id(shipmentMovement.getId())
                .shipmentType(shipmentMovement.getShipmentType())
                .postalIndex(shipmentMovement.getPostalIndex())
                .arrived(shipmentMovement.getArrived())
                .build();
    }

    public ShipmentMovementDto mapToDtoWithDeparture(ShipmentMovement shipmentMovement) {
        return ShipmentMovementDto.builder()
                .id(shipmentMovement.getId())
                .shipmentType(shipmentMovement.getShipmentType())
                .postalIndex(shipmentMovement.getPostalIndex())
                .arrived(shipmentMovement.getArrived())
                .departed(shipmentMovement.getDeparted())
                .build();
    }

    public ShipmentMoveHistoryDto mapToDtoWithHistory(Shipment shipment, List<ShipmentMovementDto> movements) {
        return ShipmentMoveHistoryDto.builder()
                .id(shipment.getId())
                .type(shipment.getType())
                .recipientAddress(shipment.getRecipientAddress())
                .recipientIndex(shipment.getRecipientIndex())
                .recipientName(shipment.getRecipientName())
                .movements(movements)
                .build();
    }
}
