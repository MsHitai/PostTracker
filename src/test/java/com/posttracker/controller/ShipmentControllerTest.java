package com.posttracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.posttracker.dto.ShipmentDto;
import com.posttracker.dto.ShipmentMoveHistoryDto;
import com.posttracker.dto.ShipmentMovementDto;
import com.posttracker.exceptions.DataNotFoundException;
import com.posttracker.mapper.ShipmentMovementMapper;
import com.posttracker.model.PostalOffice;
import com.posttracker.model.ShipmentMovement;
import com.posttracker.model.Type;
import com.posttracker.service.ShipmentService;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShipmentController.class)
class ShipmentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ShipmentService service;

    private ShipmentDto dto;
    private Long shipmentId;
    private ShipmentMoveHistoryDto historyDto;
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

        ShipmentMovementDto dto1 = ShipmentMovementMapper.mapToDto(movement);

        historyDto = ShipmentMoveHistoryDto.builder()
                .id(1L)
                .type(dto.getType())
                .recipientName(dto.getRecipientName())
                .recipientIndex(dto.getRecipientIndex())
                .recipientAddress(dto.getRecipientAddress())
                .movements(List.of(dto1))
                .build();
    }

    @Test
    void contextLoad() {
        assertThat(service).isNotNull();
    }

    @Test
    void testAddShipmentOkWhenValid() throws Exception {
        when(service.addShipment(dto))
                .thenReturn(dto);

        mvc.perform(post("/shipments")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.type", is(dto.getType().toString())))
                .andExpect(jsonPath("$.recipientIndex", is(dto.getRecipientIndex())))
                .andExpect(jsonPath("$.recipientAddress", is(dto.getRecipientAddress())))
                .andExpect(jsonPath("$.recipientName", is(dto.getRecipientName())));
    }

    @Test
    void testAddShipmentFailWhenTypeNull() throws Exception {
        dto.setType(null);
        when(service.addShipment(dto))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/shipments")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAddShipmentFailWhenAddressNull() throws Exception {
        dto.setRecipientAddress(null);
        when(service.addShipment(dto))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/shipments")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testRegisterArrivalOkWhenValid() throws Exception {
        ShipmentMovementDto movementDto = ShipmentMovementMapper.mapToDto(movement);

        when(service.registerArrival(office1.getId(), shipmentId))
                .thenReturn(movementDto);

        mvc.perform(put("/shipments/" + shipmentId + "/arrival")
                        .header("X-PTracker-Office-Id", office1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movementDto.getId()), Long.class))
                .andExpect(jsonPath("$.shipmentType", is(movementDto.getShipmentType().toString())))
                .andExpect(jsonPath("$.postalIndex", is(movementDto.getPostalIndex())))
                .andExpect(jsonPath("$.arrived", is(movementDto.getArrived().toString())));
    }

    @Test
    void testRegisterArrival404WhenWrongShipmentId() throws Exception {
        Long wrongId = 22L;

        when(service.registerArrival(office1.getId(), wrongId))
                .thenThrow(DataNotFoundException.class);

        mvc.perform(put("/shipments/" + wrongId + "/arrival")
                        .header("X-PTracker-Office-Id", office1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testRegisterArrival404WhenWrongOfficeId() throws Exception {
        Long wrongId = 22L;

        when(service.registerArrival(wrongId, shipmentId))
                .thenThrow(DataNotFoundException.class);

        mvc.perform(put("/shipments/" + shipmentId + "/arrival")
                        .header("X-PTracker-Office-Id", wrongId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testRegisterDepartureOkWhenValid() throws Exception {
        movement.setDeparted(time.plusDays(2));
        ShipmentMovementDto movementDto = ShipmentMovementMapper.mapToDtoWithDeparture(movement);

        when(service.registerDeparture(office1.getId(), shipmentId, movement.getId()))
                .thenReturn(movementDto);

        mvc.perform(put("/shipments/" + shipmentId + "/departure")
                        .header("X-PTracker-Office-Id", office1.getId())
                        .param("movementId", String.valueOf(movement.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movementDto.getId()), Long.class))
                .andExpect(jsonPath("$.shipmentType", is(movementDto.getShipmentType().toString())))
                .andExpect(jsonPath("$.postalIndex", is(movementDto.getPostalIndex())))
                .andExpect(jsonPath("$.arrived", is(movementDto.getArrived().toString())))
                .andExpect(jsonPath("$.departed", is(movementDto.getDeparted().toString())));
    }

    @Test
    void testRegisterDepartureFailWhenWrongMovementId() throws Exception {
        Long wrongId = 22L;
        movement.setDeparted(time.plusDays(2));

        when(service.registerDeparture(office1.getId(), shipmentId, wrongId))
                .thenThrow(DataNotFoundException.class);

        mvc.perform(put("/shipments/" + shipmentId + "/departure")
                        .header("X-PTracker-Office-Id", office1.getId())
                        .param("movementId", String.valueOf(wrongId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testFindByIdOkWhenValid() throws Exception {
        when(service.findById(shipmentId))
                .thenReturn(historyDto);

        mvc.perform(get("/shipments/" + shipmentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(historyDto.getId()), Long.class))
                .andExpect(jsonPath("$.type", is(historyDto.getType().toString())))
                .andExpect(jsonPath("$.recipientIndex", is(historyDto.getRecipientIndex())))
                .andExpect(jsonPath("$.recipientAddress", is(historyDto.getRecipientAddress())))
                .andExpect(jsonPath("$.recipientName", is(historyDto.getRecipientName())))
                .andExpect(jsonPath("$.movements", hasSize(1)))
                .andExpect(jsonPath("$.movements[0].id", is(movement.getId()), Long.class))
                .andExpect(jsonPath("$.movements[0].shipmentType", is(movement.getShipmentType().toString())))
                .andExpect(jsonPath("$.movements[0].postalIndex", is(movement.getPostalIndex())))
                .andExpect(jsonPath("$.movements[0].arrived", is(movement.getArrived().toString())));
    }

    @Test
    void testReceiveShipmentOkWhenValid() throws Exception {
        dto.setReceived(true);
        when(service.receiveShipment(office2.getId(), shipmentId, true))
                .thenReturn(dto);

        mvc.perform(put("/shipments/" + shipmentId + "/receive")
                        .header("X-PTracker-Office-Id", office2.getId())
                        .param("received", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.type", is(dto.getType().toString())))
                .andExpect(jsonPath("$.recipientIndex", is(dto.getRecipientIndex())))
                .andExpect(jsonPath("$.recipientAddress", is(dto.getRecipientAddress())))
                .andExpect(jsonPath("$.recipientName", is(dto.getRecipientName())))
                .andExpect(jsonPath("$.received", is(dto.getReceived())));
    }
}