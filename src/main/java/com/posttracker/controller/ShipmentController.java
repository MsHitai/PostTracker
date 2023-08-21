package com.posttracker.controller;

import com.posttracker.dto.ShipmentDto;
import com.posttracker.dto.ShipmentMoveHistoryDto;
import com.posttracker.dto.ShipmentMovementDto;
import com.posttracker.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping()
    public ShipmentDto addShipment(@Valid @RequestBody ShipmentDto shipmentDto) {
        log.info("POST request on adding a shipment {}", shipmentDto.toString());
        return shipmentService.addShipment(shipmentDto);
    }

    @PutMapping("/{shipmentId}/arrival")
    public ShipmentMovementDto registerArrival(@RequestHeader("X-PTracker-Office-Id") Long officeId,
                                               @PathVariable Long shipmentId) {
        log.info("PUT request on registering arrival of shipmentId {} in an intermediate postal office by id {}",
                shipmentId, officeId);
        return shipmentService.registerArrival(officeId, shipmentId);
    }

    @PutMapping("/{shipmentId}/departure")
    public ShipmentMovementDto registerDeparture(@RequestHeader("X-PTracker-Office-Id") Long officeId,
                                                 @PathVariable Long shipmentId,
                                                 @RequestParam("movementId") Long movementId) {
        log.info("PUT request on registering departure of shipmentId {} in an intermediate postal office by id {}",
                shipmentId, officeId);
        return shipmentService.registerDeparture(officeId, shipmentId, movementId);
    }

    @GetMapping("/{shipmentId}")
    public ShipmentMoveHistoryDto findById(@PathVariable Long shipmentId) {
        log.info("GET request for a shipment by id {}", shipmentId);
        return shipmentService.findById(shipmentId);
    }

    @PutMapping("/{shipmentId}/receive")
    public ShipmentDto receiveShipment(@RequestHeader("X-PTracker-Office-Id") Long officeId,
                                       @PathVariable Long shipmentId,
                                       @RequestParam("received") Boolean received) {
        log.info("PUT request to receive the shipment by id {} at postal office by id {}", shipmentId, officeId);
        return shipmentService.receiveShipment(officeId, shipmentId, received);
    }
}
