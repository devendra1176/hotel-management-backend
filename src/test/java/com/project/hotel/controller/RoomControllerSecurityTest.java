package com.project.hotel.controller;

import com.project.hotel.dto.ApiResponse;
import com.project.hotel.dto.RoomResponseDTO;
import com.project.hotel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.springframework.data.domain.Page;

import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



class RoomControllerSecurityTest {

    private MockMvc mockMvc;
    private RoomService roomService;
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        roomService = mock(RoomService.class);
        roomController = new RoomController(roomService);
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
                .andExpect(jsonPath("$.message").value("Room created successfully"))
                .andExpect(jsonPath("$.data.roomNumber").value("101"));

        verify(roomService).createRoom(any());
    }

    @Test
    void deleteRoom_shouldReturnSuccess() throws Exception {

        mockMvc.perform(delete("/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Room deleted successfully"));

        verify(roomService).deleteRoom(1L);
    }

    @Test
    void getAvailableRoomsByDate_shouldReturnRooms() {

        Page<RoomResponseDTO> page = Page.empty();

        when(roomService.getAvailableRoomsByDate(
                any(),
                any(),
                anyInt(),
                anyInt(),
                any()
        )).thenReturn(page);

        ApiResponse<Page<RoomResponseDTO>> response =
                roomController.getAvailableRoomsByDate(
                        "2026-05-10",
                        "2026-05-12",
                        0,
                        5,
                        "price"
                );

        assertEquals(200, response.getStatus());
        assertEquals("Available rooms fetched successfully", response.getMessage());
        assertNotNull(response.getData());

        verify(roomService).getAvailableRoomsByDate(
                "2026-05-10",
                "2026-05-12",
                0,
                5,
                "price"
        );
    }

}