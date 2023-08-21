package com.posttracker.service;

import com.posttracker.dto.ShipmentDto;
import com.posttracker.dto.ShipmentMoveHistoryDto;
import com.posttracker.dto.ShipmentMovementDto;

public interface ShipmentService {
    ShipmentDto addShipment(ShipmentDto shipmentDto);

    ShipmentMovementDto registerArrival(Long index, Long shipmentId);

    ShipmentMovementDto registerDeparture(Long officeId, Long shipmentId, Long movementId);

    ShipmentMoveHistoryDto findById(Long shipmentId);

    ShipmentDto receiveShipment(Long officeId, Long shipmentId, Boolean received);

}
