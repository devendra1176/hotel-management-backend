package com.project.hotel.service;

import com.project.hotel.controller.RoomType;
import com.project.hotel.dto.RoomRequestDTO;
import com.project.hotel.dto.RoomResponseDTO;
import com.project.hotel.entity.Room;
import com.project.hotel.exception.*;
import com.project.hotel.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public RoomResponseDTO createRoom(RoomRequestDTO dto) {

        log.info("Room creation requested: roomNumber={}, type={}, price={}",
                dto.getRoomNumber(), dto.getType(), dto.getPrice());

        if (roomRepository.existsByRoomNumber(dto.getRoomNumber())) {
            log.warn("Room creation rejected: duplicate roomNumber={}", dto.getRoomNumber());
            throw new RoomAlreadyExistsException("Room already exists");
        }

        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setType(parseRoomType(dto.getType()));
        room.setPrice(dto.getPrice());
        room.setAvailable(true);

        Room saved = roomRepository.save(room);

        log.info("Room created successfully: roomId={}, roomNumber={}, type={}, price={}",
                saved.getId(), saved.getRoomNumber(), saved.getType(), saved.getPrice());

        return mapToDTO(saved);
    }

    public Page<RoomResponseDTO> searchRooms(String type, double maxPrice, int page, int size) {

        log.info("Room search requested: type={}, maxPrice={}, page={}, size={}",
                type, maxPrice, page, size);

        RoomType roomType = parseRoomType(type);

        Pageable pageable = PageRequest.of(page, size);

        Page<Room> roomPage =
                roomRepository.findByAvailableTrueAndTypeAndPriceLessThanEqual(
                        roomType,
                        maxPrice,
                        pageable
                );

        log.info("Room search completed: type={}, maxPrice={}, totalElements={}, totalPages={}",
                roomType, maxPrice, roomPage.getTotalElements(), roomPage.getTotalPages());

        return roomPage.map(this::mapToDTO);
    }

    public List<RoomResponseDTO> searchRoomsDynamic(
            String type,
            Double maxPrice,
            Boolean available) {

        log.info("Dynamic room search requested: type={}, maxPrice={}, available={}",
                type, maxPrice, available);

        Specification<Room> spec = (root, query, cb) -> cb.conjunction();

        if (type != null && !type.trim().isEmpty()) {
            RoomType roomType = parseRoomType(type);

            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("type"), roomType));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        if (available != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("available"), available));
        }

        List<Room> rooms = roomRepository.findAll(spec);

        log.info("Dynamic room search completed: resultCount={}", rooms.size());

        return rooms.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Page<RoomResponseDTO> getAllRooms(int page, int size, String sortBy) {

        log.info("Room list requested: page={}, size={}, sortBy={}",
                page, size, sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<Room> roomPage = roomRepository.findAll(pageable);

        log.info("Room list fetched: totalElements={}, totalPages={}, page={}",
                roomPage.getTotalElements(), roomPage.getTotalPages(), page);

        return roomPage.map(this::mapToDTO);
    }

    public void deleteRoom(Long id) {

        log.info("Room delete requested: roomId={}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Room delete failed: room not found, roomId={}", id);
                    return new RoomNotFoundException("Room not found");
                });

        roomRepository.delete(room);

        log.info("Room deleted successfully: roomId={}, roomNumber={}",
                id, room.getRoomNumber());
    }

    public Page<RoomResponseDTO> searchAdvanced(
            String checkInStr,
            String checkOutStr,
            String typeStr,
            Double maxPrice,
            int page,
            int size,
            String sortBy) {

        log.info("Advanced room search requested: checkIn={}, checkOut={}, type={}, maxPrice={}, page={}, size={}, sortBy={}",
                checkInStr, checkOutStr, typeStr, maxPrice, page, size, sortBy);

        LocalDate checkIn = LocalDate.parse(checkInStr);
        LocalDate checkOut = LocalDate.parse(checkOutStr);

        validateDateRange(checkIn, checkOut);

        RoomType type = null;

        if (typeStr != null && !typeStr.trim().isEmpty()) {
            type = parseRoomType(typeStr);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<Room> rooms = roomRepository.searchAvailableRooms(
                checkIn,
                checkOut,
                type,
                maxPrice,
                pageable
        );

        log.info("Advanced room search completed: totalElements={}, totalPages={}, type={}, maxPrice={}",
                rooms.getTotalElements(), rooms.getTotalPages(), type, maxPrice);

        return rooms.map(this::mapToDTO);
    }

    public Page<RoomResponseDTO> getAvailableRoomsByDate(
            String checkInStr,
            String checkOutStr,
            int page,
            int size,
            String sortBy) {

        log.info("Available rooms by date requested: checkIn={}, checkOut={}, page={}, size={}, sortBy={}",
                checkInStr, checkOutStr, page, size, sortBy);

        LocalDate checkIn = LocalDate.parse(checkInStr);
        LocalDate checkOut = LocalDate.parse(checkOutStr);

        validateDateRange(checkIn, checkOut);

        if (checkIn.isBefore(LocalDate.now())) {
            log.warn("Available room search rejected: check-in in past, checkIn={}", checkIn);
            throw new InvalidDateRangeException("Check-in cannot be in the past");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy).ascending()
        );

        Page<Room> rooms = roomRepository.findAvailableRoomsByDate(
                checkIn,
                checkOut,
                pageable
        );

        log.info("Available rooms by date fetched: checkIn={}, checkOut={}, totalElements={}, totalPages={}",
                checkIn, checkOut, rooms.getTotalElements(), rooms.getTotalPages());

        return rooms.map(this::mapToDTO);
    }

    private RoomType parseRoomType(String type) {

        try {
            return RoomType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            log.warn("Invalid room type received: type={}", type);
            throw new InvalidRoomTypeException("Invalid room type");
        }
    }

    private void validateDateRange(LocalDate checkIn, LocalDate checkOut) {

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            log.warn("Invalid date range: checkIn={}, checkOut={}", checkIn, checkOut);
            throw new InvalidDateRangeException("Check-out date must be after check-in date");
        }
    }

    private RoomResponseDTO mapToDTO(Room room) {

        RoomResponseDTO dto = new RoomResponseDTO();

        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setType(room.getType().name());
        dto.setPrice(room.getPrice());
        dto.setAvailable(room.isAvailable());

        return dto;
    }
}