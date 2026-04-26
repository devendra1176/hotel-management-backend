package com.project.hotel.service;

import com.project.hotel.controller.RoomType;
import com.project.hotel.dto.RoomRequestDTO;
import com.project.hotel.dto.RoomResponseDTO;
import com.project.hotel.entity.Room;
import com.project.hotel.exception.InvalidRoomTypeException;
import com.project.hotel.exception.RoomAlreadyExistsException;
import com.project.hotel.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void createRoom_shouldThrowException_whenRoomAlreadyExists() {

        // ARRANGE
        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("101");
        dto.setType("DELUXE");
        dto.setPrice(2000);

        when(roomRepository.existsByRoomNumber("101"))
                .thenReturn(true);

        // ACT + ASSERT
        assertThrows(
                RoomAlreadyExistsException.class,
                () -> roomService.createRoom(dto)
        );

        // VERIFY
        verify(roomRepository, never()).save(any());
    }

    @Test
    void createRoom_shouldCreateRoomSuccessfully() {

        // ARRANGE

        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("102");
        dto.setType("DELUXE");
        dto.setPrice(2500);

        // Fake saved room (DB will return)
        Room savedRoom = new Room();
        savedRoom.setId(1L);
        savedRoom.setRoomNumber("102");
        savedRoom.setType(RoomType.DELUXE);
        savedRoom.setPrice(2500);
        savedRoom.setAvailable(true);

        when(roomRepository.existsByRoomNumber("102"))
                .thenReturn(false);

        when(roomRepository.save(any(Room.class)))
                .thenReturn(savedRoom);

        // ACT

        RoomResponseDTO response =
                roomService.createRoom(dto);

        // ASSERT

        assertEquals("102", response.getRoomNumber());
        assertEquals("DELUXE", response.getType());
        assertEquals(2500, response.getPrice());

        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void createRoom_shouldThrowException_whenRoomTypeIsInvalid() {

        // ARRANGE
        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("103");
        dto.setType("KING"); // invalid type
        dto.setPrice(3000);

        when(roomRepository.existsByRoomNumber("103"))
                .thenReturn(false);

        // ACT + ASSERT
        assertThrows(
                InvalidRoomTypeException.class,
                () -> roomService.createRoom(dto)
        );

        // VERIFY
        verify(roomRepository, never()).save(any());
    }

    @Test
    void createRoom_shouldSetAvailableTrueByDefault() {

        // ARRANGE

        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("104");
        dto.setType("DELUXE");
        dto.setPrice(2500);

        when(roomRepository.existsByRoomNumber("104"))
                .thenReturn(false);

        Room savedRoom = new Room();
        savedRoom.setId(1L);
        savedRoom.setRoomNumber("104");
        savedRoom.setType(RoomType.DELUXE);
        savedRoom.setPrice(2500);
        savedRoom.setAvailable(true); // expected

        when(roomRepository.save(any(Room.class)))
                .thenReturn(savedRoom);

        // ACT

        RoomResponseDTO result = roomService.createRoom(dto);

        // ASSERT

        assertTrue(result.isAvailable());

        // VERIFY

        verify(roomRepository, times(1)).save(any(Room.class));
    }


}