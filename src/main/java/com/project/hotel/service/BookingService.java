package com.project.hotel.service;

import com.project.hotel.dto.BookingRequestDTO;
import com.project.hotel.dto.BookingResponseDTO;
import com.project.hotel.entity.Booking;
import com.project.hotel.entity.Room;
import com.project.hotel.entity.User;
import com.project.hotel.exception.*;
import com.project.hotel.repository.BookingRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    private final EmailService emailService;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          RoomRepository roomRepository,
                          EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.emailService = emailService;
    }

    public BookingResponseDTO createBooking(BookingRequestDTO dto, String email) {

        log.info("Booking creation requested: email={}, roomId={}, checkIn={}, checkOut={}",
                email, dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut());

        LocalDate checkIn = dto.getCheckIn();
        LocalDate checkOut = dto.getCheckOut();

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            log.warn("Booking rejected due to invalid date range: email={}, checkIn={}, checkOut={}",
                    email, checkIn, checkOut);
            throw new InvalidDateRangeException("Check-out date must be after check-in date");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            log.warn("Booking rejected because check-in is in the past: email={}, checkIn={}",
                    email, checkIn);
            throw new InvalidDateRangeException("Check-in cannot be in the past");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Booking failed: user not found, email={}", email);
                    return new UserNotFoundException("User not found");
                });

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> {
                    log.warn("Booking failed: room not found, roomId={}, requestedBy={}",
                            dto.getRoomId(), email);
                    return new RoomNotFoundException("Room not found");
                });

        boolean isAlreadyBooked =
                bookingRepository.existsOverlappingBooking(room, checkIn, checkOut);

        if (isAlreadyBooked) {
            log.warn("Booking rejected: room already booked. roomId={}, roomNumber={}, checkIn={}, checkOut={}, requestedBy={}",
                    room.getId(), room.getRoomNumber(), checkIn, checkOut, email);
            throw new RoomAlreadyBookedException("Room is already booked for selected dates");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);

        Booking saved = bookingRepository.save(booking);
        emailService.sendBookingConfirmationEmail(saved);

        log.info("Booking created successfully: bookingId={}, userId={}, email={}, roomId={}, roomNumber={}, checkIn={}, checkOut={}",
                saved.getId(), user.getId(), email, room.getId(), room.getRoomNumber(), checkIn, checkOut);

        return mapToResponse(saved);
    }

    public void cancelBooking(Long bookingId, String email) {

        log.info("Booking cancellation requested: bookingId={}, requestedBy={}", bookingId, email);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking cancellation failed: booking not found, bookingId={}, requestedBy={}",
                            bookingId, email);
                    return new BookingNotFoundException("Booking not found");
                });

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Booking cancellation failed: user not found, email={}", email);
                    return new UserNotFoundException("User not found");
                });

        boolean isOwner = booking.getUser().getId().equals(loggedInUser.getId());
        boolean isAdmin = loggedInUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            log.warn("Unauthorized booking cancel attempt: bookingId={}, ownerId={}, requestedUserId={}, requestedEmail={}, role={}",
                    bookingId,
                    booking.getUser().getId(),
                    loggedInUser.getId(),
                    email,
                    loggedInUser.getRole().name());

            throw new UnauthorizedBookingAccessException("You are not allowed to cancel this booking");
        }

        bookingRepository.delete(booking);

        log.info("Booking cancelled successfully: bookingId={}, cancelledBy={}, role={}",
                bookingId, email, loggedInUser.getRole().name());
    }

    public Page<BookingResponseDTO> getMyBookingHistory(
            String email,
            int page,
            int size,
            String sortBy) {

        log.info("My booking history requested: email={}, page={}, size={}, sortBy={}",
                email, page, size, sortBy);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Booking history failed: user not found, email={}", email);
                    return new UserNotFoundException("User not found");
                });

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Booking> bookings = bookingRepository.findByUser(user, pageable);

        log.info("My booking history fetched: email={}, totalElements={}, totalPages={}",
                email, bookings.getTotalElements(), bookings.getTotalPages());

        return bookings.map(this::mapToResponse);
    }

    public Page<BookingResponseDTO> getAllBookingHistory(
            int page,
            int size,
            String sortBy) {

        log.info("All booking history requested by ADMIN: page={}, size={}, sortBy={}",
                page, size, sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Booking> bookings = bookingRepository.findAll(pageable);

        log.info("All booking history fetched: totalElements={}, totalPages={}",
                bookings.getTotalElements(), bookings.getTotalPages());

        return bookings.map(this::mapToResponse);
    }

    private BookingResponseDTO mapToResponse(Booking booking) {

        BookingResponseDTO res = new BookingResponseDTO();

        res.setBookingId(booking.getId());
        res.setUserName(booking.getUser().getName());
        res.setRoomNumber(booking.getRoom().getRoomNumber());
        res.setCheckIn(booking.getCheckIn().toString());
        res.setCheckOut(booking.getCheckOut().toString());

        return res;
    }
}