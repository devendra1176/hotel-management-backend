package com.project.hotel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkIn;

    private LocalDate checkOut;

    // MANY BOOKINGS → ONE USER
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // MANY BOOKINGS → ONE ROOM
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}