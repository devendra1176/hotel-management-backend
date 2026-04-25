package com.project.hotel.service;

import com.project.hotel.dto.AdminStatsDTO;
import com.project.hotel.repository.BookingRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public AdminService(UserRepository userRepository,
                        RoomRepository roomRepository,
                        BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    public AdminStatsDTO getAdminStats() {

        long totalUsers = userRepository.count();
        long totalRooms = roomRepository.count();
        long totalBookings = bookingRepository.count();
        long availableRooms = roomRepository.countByAvailableTrue();

        return new AdminStatsDTO(
                totalUsers,
                totalRooms,
                totalBookings,
                availableRooms
        );
    }
}