package com.project.hotel.repository;

import com.project.hotel.controller.RoomType;
import com.project.hotel.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository
        extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    boolean existsByRoomNumber(String roomNumber);
    Page<Room> findByAvailableTrueAndTypeAndPriceLessThanEqual(
            RoomType type,
            double price,
            Pageable pageable
    );
    @Query("""
    SELECT r FROM Room r
    WHERE r.id NOT IN (
        SELECT b.room.id FROM Booking b
        WHERE (:checkIn < b.checkOut AND :checkOut > b.checkIn)
    )
""")
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut);

    @Query("""
SELECT r FROM Room r
WHERE r.id NOT IN (
    SELECT b.room.id FROM Booking b
    WHERE (:checkIn < b.checkOut AND :checkOut > b.checkIn)
)
AND (:type IS NULL OR r.type = :type)
AND (:maxPrice IS NULL OR r.price <= :maxPrice)
""")
    Page<Room> searchAvailableRooms(
            LocalDate checkIn,
            LocalDate checkOut,
            RoomType type,
            Double maxPrice,
            Pageable pageable
    );

    @Query("""
        SELECT r FROM Room r
        WHERE r.id NOT IN (
            SELECT b.room.id FROM Booking b
            WHERE (:checkIn < b.checkOut AND :checkOut > b.checkIn)
        )
        """)
    Page<Room> findAvailableRoomsByDate(
            LocalDate checkIn,
            LocalDate checkOut,
            Pageable pageable
    );
}