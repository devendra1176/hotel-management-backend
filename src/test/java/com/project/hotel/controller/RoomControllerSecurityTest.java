package com.project.hotel.controller;

import com.project.hotel.dto.RoomResponseDTO;
import com.project.hotel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class RoomControllerSecurityTest {

    private MockMvc mockMvc;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomService = mock(RoomService.class);
        RoomController roomController = new RoomController(roomService);
        mockMvc = standaloneSetup(roomController).build();
    }

    @Test
    void createRoom_shouldReturnSuccess() throws Exception {

        String requestJson = """
                {
                  "roomNumber": "101",
                  "type": "DELUXE",
                  "price": 2500
                }
                """;

        RoomResponseDTO response = new RoomResponseDTO();
        response.setId(1L);
        response.setRoomNumber("101");
        response.setType("DELUXE");
        response.setPrice(2500);
        response.setAvailable(true);

        when(roomService.createRoom(any())).thenReturn(response);

        mockMvc.perform(
                        post("/rooms")
                                .contentType(APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Room created"))
                .andExpect(jsonPath("$.data.roomNumber").value("101"));

        verify(roomService).createRoom(any());
    }
}