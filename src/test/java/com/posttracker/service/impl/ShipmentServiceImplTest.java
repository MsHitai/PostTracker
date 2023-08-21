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
import com.posttracker.model.Type;
import com.posttracker.repository.PostalOfficeRepository;
import com.posttracker.repository.ShipmentMovementRepository;
import com.posttracker.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceImplTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private PostalOfficeRepository officeRepository;

    @Mock
    private ShipmentMovementRepository movementRepository;

    @InjectMocks
    private ShipmentServiceImpl service;

    private ShipmentDto dto;
    private Shipment shipment;
    private Long shipmentId;
    private ShipmentMovement movement;
    private PostalOffice office1;
    private PostalOffice office2;

    private final LocalDateTime time =
            LocalDateTime.of(2023, Month.AUGUST, 21, 9, 10, 1);

    @BeforeEach
    void setUp() {

        shipmentId = 1L;

        office1 = new PostalOffice(
                1L,
                603148,
                "Test Post",
                "Test Post Address"
        );

        office2 = new PostalOffice(
                2L,
                603158,
                "Test Name",
                "Test Address"
        );

        dto = new ShipmentDto(
                shipmentId,
                Type.LETTER,
                603158,
                "Test Address",
                "Test Name",
                false
        );

        movement = ShipmentMovement.builder()
                .id(1L)
                .shipmentType(dto.getType())
                .postalIndex(office1.getIndex())
                .arrived(time)
                .build();

        shipment = Shipment.builder()
                .id(shipmentId)
                .type(Type.LETTER)
                .recipientIndex(dto.getRecipientIndex())
                .recipientAddress(dto.getRecipientAddress())
                .recipientName(dto.getRecipientName())
                .movements(new ArrayList<>())
                .received(false)
                .build();
    }

    @Test
    void testAddShipmentOkWhenValid() {
        when(shipmentRepository.save(any(Shipment.class)))
                .thenReturn(shipment);

        ShipmentDto actualShipment = service.addShipment(dto);

        assertThat(actualShipment.getId(), is(dto.getId()));
        assertThat(actualShipment.getType(), is(dto.getType()));
        assertThat(actualShipment.getRecipientName(), is(dto.getRecipientName()));
        assertThat(actualShipment.getRecipientAddress(), is(dto.getRecipientAddress()));
        assertThat(actualShipment.getRecipientIndex(), is(dto.getRecipientIndex()));

        verify(shipmentRepository, times(1))
                .save(any(Shipment.class));
    }

    @Test
    void testRegisterArrivalOkWhenValid() {
        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.ofNullable(shipment));
        when(officeRepository.findById(office1.getId()))
                .thenReturn(Optional.ofNullable(office1));
        when(movementRepository.save(any(ShipmentMovement.class)))
                .thenReturn(movement);

        ShipmentMovementDto actualShipment = service.registerArrival(office1.getId(), shipmentId);

        assertThat(actualShipment.getId(), is(movement.getId()));
        assertThat(actualShipment.getShipmentType(), is(movement.getShipmentType()));
        assertThat(actualShipment.getPostalIndex(), is(movement.getPostalIndex()));
        assertThat(actualShipment.getArrived(), is(movement.getArrived()));
    }

    @Test
    void testRegisterArrivalFailWhenWrongShipmentId() {
        Long wrongId = 22L;
        when(shipmentRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.registerArrival(office1.getId(), wrongId));
    }

    @Test
    void testRegisterArrivalFailWhenWrongOfficeId() {
        Long wrongId = 22L;
        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.ofNullable(shipment));
        when(officeRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.registerArrival(wrongId, shipmentId));
    }

    @Test
    void testRegisterDepartureOkWhenValid() {
        shipment.getMovements().add(movement);
        ShipmentMovementDto newDto = ShipmentMovementMapper.mapToDto(movement);
        newDto.setDeparted(time.plusDays(2));
        ShipmentMovement newMovement = ShipmentMovementMapper.mapToShipmentMovement(newDto, shipment);
        newMovement.setDeparted(time.plusDays(2));

        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.ofNullable(shipment));
        when(officeRepository.findById(office1.getId()))
                .thenReturn(Optional.ofNullable(office1));
        when(movementRepository.findById(movement.getId()))
                .thenReturn(Optional.ofNullable(movement));
        when(movementRepository.save(any(ShipmentMovement.class)))
                .thenReturn(newMovement);

        ShipmentMovementDto actualShipment = service.registerDeparture(office1.getId(), shipmentId, movement.getId());

        assertThat(actualShipment.getId(), is(newDto.getId()));
        assertThat(actualShipment.getShipmentType(), is(newDto.getShipmentType()));
        assertThat(actualShipment.getPostalIndex(), is(newDto.getPostalIndex()));
        assertThat(actualShipment.getArrived(), is(newDto.getArrived()));
        assertThat(actualShipment.getDeparted(), is(newDto.getDeparted()));
    }

    @Test
    void testFindByIdOkWhenValid() {
        shipment.getMovements().add(movement);
        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.ofNullable(shipment));

        ShipmentMoveHistoryDto actualShipment = service.findById(shipmentId);

        assertThat(actualShipment.getId(), is(dto.getId()));
        assertThat(actualShipment.getType(), is(dto.getType()));
        assertThat(actualShipment.getRecipientName(), is(dto.getRecipientName()));
        assertThat(actualShipment.getMovements().size(), is(1));
    }

    @Test
    void testReceiveShipmentOkWhenValid() {
        Shipment newShipment = ShipmentMapper.mapToShipment(dto);
        newShipment.setReceived(true);
        dto.setReceived(true);
        when(shipmentRepository.findById(shipmentId))
                .thenReturn(Optional.ofNullable(shipment));
        when(officeRepository.findById(office2.getId()))
                .thenReturn(Optional.ofNullable(office2));
        when(shipmentRepository.save(any(Shipment.class)))
                .thenReturn(newShipment);

        ShipmentDto actualShipment = service.receiveShipment(office2.getId(), shipmentId, true);

        assertThat(actualShipment.getId(), is(dto.getId()));
        assertThat(actualShipment.getType(), is(dto.getType()));
        assertThat(actualShipment.getRecipientName(), is(dto.getRecipientName()));
        assertThat(actualShipment.getRecipientAddress(), is(dto.getRecipientAddress()));
        assertThat(actualShipment.getRecipientIndex(), is(dto.getRecipientIndex()));
        assertThat(actualShipment.getReceived(), is(dto.getReceived()));

        verify(shipmentRepository, times(1))
                .save(any(Shipment.class));
    }
}