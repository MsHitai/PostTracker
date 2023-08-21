package com.posttracker.service.impl;

import com.posttracker.dto.ShipmentDto;
import com.posttracker.dto.ShipmentMoveHistoryDto;
import com.posttracker.dto.ShipmentMovementDto;
import com.posttracker.exceptions.DataNotFoundException;
import com.posttracker.mapper.ShipmentMapper;
import com.posttracker.mapper.ShipmentMovementMapper;
import com.posttracker.model.PostalOffice;
import com.posttracker.model.Shipment;
import com.posttracker.model.ShipmentMovement;
import com.posttracker.repository.PostalOfficeRepository;
import com.posttracker.repository.ShipmentMovementRepository;
import com.posttracker.repository.ShipmentRepository;
import com.posttracker.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final PostalOfficeRepository officeRepository;
    private final ShipmentMovementRepository movementRepository;

    @Override
    public ShipmentDto addShipment(ShipmentDto shipmentDto) {
        Shipment shipment = ShipmentMapper.mapToShipment(shipmentDto);
        return ShipmentMapper.mapToDto(shipmentRepository.save(shipment));
    }

    @Override
    public ShipmentMovementDto registerArrival(Long officeId, Long shipmentId) {
        Shipment shipment = checkShipmentId(shipmentId);
        PostalOffice office = checkOfficeId(officeId);
        ShipmentMovement movement = ShipmentMovement.builder()
                .shipmentType(shipment.getType())
                .postalIndex(office.getIndex())
                .arrived(LocalDateTime.now())
                .shipment(shipment)
                .build();
        return ShipmentMovementMapper.mapToDto(movementRepository.save(movement));
    }

    @Override
    public ShipmentMovementDto registerDeparture(Long officeId, Long shipmentId, Long movementId) {
        checkShipmentId(shipmentId);
        checkOfficeId(officeId);
        ShipmentMovement movement = checkMovementId(movementId);
        LocalDateTime now = LocalDateTime.now();
        movement.setDeparted(now);
        return ShipmentMovementMapper.mapToDtoWithDeparture(movementRepository.save(movement));
    }

    @Override
    public ShipmentMoveHistoryDto findById(Long shipmentId) {
        Shipment shipment = checkShipmentId(shipmentId);
        List<ShipmentMovement> movements = shipment.getMovements();
        List<ShipmentMovementDto> movementDtos = movements.stream()
                .map(ShipmentMovementMapper::mapToDtoWithDeparture)
                .toList();
        return ShipmentMovementMapper.mapToDtoWithHistory(shipment, movementDtos);
    }

    @Override
    public ShipmentDto receiveShipment(Long officeId, Long shipmentId, Boolean received) {
        Shipment shipment = checkShipmentId(shipmentId);
        checkOfficeId(officeId);
        shipment.setReceived(received);
        return ShipmentMapper.mapToDto(shipmentRepository.save(shipment));
    }

    private Shipment checkShipmentId(Long shipmentId) {
        return shipmentRepository.findById(shipmentId).orElseThrow(() -> new DataNotFoundException("Shipment with " +
                "the id " + shipmentId + " is not in the database"));
    }

    private PostalOffice checkOfficeId(Long officeId) {
        return officeRepository.findById(officeId).orElseThrow(() -> new DataNotFoundException("Postal office with " +
                "the id " + officeId + " is not in the database"));
    }

    private ShipmentMovement checkMovementId(Long movementId) {
        return movementRepository.findById(movementId).orElseThrow(() -> new DataNotFoundException("Movement with " +
                "the id " + movementId + " is not in the database"));
    }

}
