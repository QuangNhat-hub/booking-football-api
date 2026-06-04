package com.booking.football_api.service;

import com.booking.football_api.dto.BookingResponseDTO;
import com.booking.football_api.dto.CreateBookingRequest;
import com.booking.football_api.entity.Booking;
import com.booking.football_api.entity.Field;
import com.booking.football_api.repository.BookingRepository;
import com.booking.football_api.repository.FieldRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final FieldRepository fieldRepository;

    // Constructor injection - tốt hơn @Autowired
    public BookingService(BookingRepository bookingRepository, FieldRepository fieldRepository) {
        this.bookingRepository = bookingRepository;
        this.fieldRepository = fieldRepository;
    }

    public List<BookingResponseDTO> getUserBookings(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID không hợp lệ");
        }
        
        List<Booking> bookings = bookingRepository.findByUserIdOrderByStartTimeDesc(userId);
        return bookings.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public boolean cancelBooking(Long bookingId, String reason) {
        if (bookingId == null || bookingId <= 0) {
            throw new IllegalArgumentException("Booking ID không hợp lệ");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do hủy không được để trống");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt với ID: " + bookingId));

        // Kiểm tra trạng thái
        String status = booking.getStatus();
        if (!("pending".equals(status) || "confirmed".equals(status))) {
            throw new IllegalArgumentException("Chỉ có thể hủy đơn đặt ở trạng thái pending hoặc confirmed");
        }

        // Kiểm tra thời gian hủy (phải cách ít nhất 24 giờ)
        LocalDateTime now = LocalDateTime.now();
        if (booking.getStartTime().isBefore(now.plusHours(24))) {
            throw new IllegalArgumentException("Chỉ có thể hủy đơn đặt trước 24 giờ bắt đầu");
        }

        booking.setStatus("cancelled");
        booking.setCancelReason(reason);
        bookingRepository.save(booking);
        return true;
    }

	private BookingResponseDTO convertToDTO(Booking booking) {
		Field field = fieldRepository.findById(booking.getPitchId().intValue()).orElse(null);
		String pitchName = (field != null) ? field.getFieldName() : "Sân không xác định";
		String pitchAddress = (field != null) ? field.getAddress() : "";
		return new BookingResponseDTO(
				   booking.getId(),
				   pitchName,
				   pitchAddress,
				   booking.getStartTime(),
				   booking.getHours(),
				   booking.getTotalPrice(),
				   booking.getStatus(),
				   booking.getCancelReason()
			   );
	}
    private boolean isConflict(
        Long pitchId,
        LocalDateTime start,
        LocalDateTime end) {

    List<Booking> bookings =
            bookingRepository.findByPitchIdAndStatusNot(
                    pitchId,
                    "cancelled");

    for (Booking booking : bookings) {

        LocalDateTime bookingStart =
                booking.getStartTime();

        LocalDateTime bookingEnd =
                bookingStart.plusHours(
                        booking.getHours());

        boolean overlap =
                start.isBefore(bookingEnd)
                && end.isAfter(bookingStart);

        if (overlap) {
            return true;
        }
    }

    return false;
}
@Transactional
public BookingResponseDTO createBooking(
        CreateBookingRequest request) {

    if (request.getHours() <= 0) {
        throw new IllegalArgumentException(
                "Số giờ phải lớn hơn 0");
    }

    Field field =
            fieldRepository.findById(
                    request.getPitchId().intValue())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Không tìm thấy sân"));

    LocalDateTime start =
            request.getStartTime();

    LocalDateTime end =
            start.plusHours(request.getHours());

    if (isConflict(
            request.getPitchId(),
            start,
            end)) {

        throw new IllegalArgumentException(
                "Khung giờ này đã được đặt");
    }

    Booking booking = new Booking();

    booking.setUserId(
            request.getUserId());

    booking.setPitchId(
            request.getPitchId());

    booking.setStartTime(
            start);

    booking.setHours(
            request.getHours());

    booking.setStatus(
            "confirmed");

    booking.setCreatedAt(
            LocalDateTime.now());

    booking.setTotalPrice(
            field.getPrice()
            * request.getHours());

    Booking saved =
            bookingRepository.save(
                    booking);

    return convertToDTO(saved);
}
}
