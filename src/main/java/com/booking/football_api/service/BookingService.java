package com.booking.football_api.service;

import com.booking.football_api.dto.BookingResponseDTO;
import com.booking.football_api.entity.Booking;
import com.booking.football_api.entity.Field;
import com.booking.football_api.repository.BookingRepository;
import com.booking.football_api.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private FieldRepository fieldRepository;  // dùng FieldRepository có sẵn

	public List<BookingResponseDTO> getUserBookings(Long userId) {
		List<Booking> bookings = bookingRepository.findByUserIdOrderByStartTimeDesc(userId);
		return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Transactional
	public boolean cancelBooking(Long bookingId, String reason) {
		Booking booking = bookingRepository.findById(bookingId)
						  .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt"));

		if (!booking.getStatus().equals("pending") && !booking.getStatus().equals("confirmed")) {
			return false;
		}

		LocalDateTime now = LocalDateTime.now();
		if (booking.getStartTime().isBefore(now.plusHours(24))) {
			return false;
		}

		booking.setStatus("cancelled");
		booking.setCancelReason(reason);
		bookingRepository.save(booking);
		return true;
	}

	private BookingResponseDTO convertToDTO(Booking booking) {
		Field field = fieldRepository.findById(booking.getPitchId()).orElse(null);
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
}
