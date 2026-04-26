package com.project.hotel.service;

import com.project.hotel.dto.BookingRequestDTO;
import com.project.hotel.dto.BookingResponseDTO;
import com.project.hotel.entity.Booking;
import com.project.hotel.entity.Role;
import com.project.hotel.entity.Room;
import com.project.hotel.entity.User;
import com.project.hotel.exception.RoomAlreadyBookedException;
import com.project.hotel.repository.BookingRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock //fake repository
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks //real BookingService with fake repositories
    private BookingService bookingService;

    @Test
    void createBooking_shouldThrowException_whenRoomAlreadyBookedForSelectedDates(){

        //ARRANGE

        String email = "dev@mail.com";

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setRoomId(1L);
        dto.setCheckIn(LocalDate.of(2026,4,26));
        dto.setCheckOut(LocalDate.of(2026,4,29));

        User user = new User();
        user.setId(1L);
        user.setName("Devendra");
        user.setEmail(email);
        user.setRole(Role.USER);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository.existsOverlappingBooking(
                room,
                LocalDate.of(2026,4,26),
                LocalDate.of(2026,4,29)
        )).thenReturn(true);

        //ACT + ASSERT

        assertThrows(
                RoomAlreadyBookedException.class,
                () -> bookingService.createBooking(dto,email)
        );

        //ASSERT extra verification

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldCreateBooking_whenNoOverlap() {

        // ARRANGE

        String email = "dev@mail.com";

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setRoomId(1L);
        dto.setCheckIn(LocalDate.of(2026, 4, 28));
        dto.setCheckOut(LocalDate.of(2026, 4, 30));

        User user = new User();
        user.setId(1L);
        user.setName("Devendra");
        user.setEmail(email);
        user.setRole(Role.USER);

        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        Booking savedBooking = new Booking();
        savedBooking.setId(10L);
        savedBooking.setUser(user);
        savedBooking.setRoom(room);
        savedBooking.setCheckIn(LocalDate.of(2026, 4, 28));
        savedBooking.setCheckOut(LocalDate.of(2026, 4, 30));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(roomRepository.findById(1L))
                .thenReturn(Optional.of(room));

        when(bookingRepository.existsOverlappingBooking(
                room,
                LocalDate.of(2026, 4, 28),
                LocalDate.of(2026, 4, 30)
        )).thenReturn(false);

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(savedBooking);

        // ACT

        BookingResponseDTO response =
                bookingService.createBooking(dto, email);

        // ASSERT

        assertEquals(10L, response.getBookingId());
        assertEquals("101", response.getRoomNumber());
        assertEquals("2026-04-28", response.getCheckIn());
        assertEquals("2026-04-30", response.getCheckOut());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
}
