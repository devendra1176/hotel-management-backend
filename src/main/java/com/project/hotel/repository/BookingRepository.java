package com.project.hotel.repository;

import com.project.hotel.entity.Booking;
import com.project.hotel.entity.Room;
import com.project.hotel.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // For booking history pagination
    Page<Booking> findByUser(User user, Pageable pageable);

    // Date overlap check for same room
    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.room = :room
            AND (:checkIn < b.checkOut AND :checkOut > b.checkIn)
            """)
    boolean existsOverlappingBooking(Room room, LocalDate checkIn, LocalDate checkOut);
}