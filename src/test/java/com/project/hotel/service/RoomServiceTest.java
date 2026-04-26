package com.project.hotel.service;

import com.project.hotel.dto.RoomRequestDTO;
import com.project.hotel.exception.RoomAlreadyExistsException;
import com.project.hotel.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
}