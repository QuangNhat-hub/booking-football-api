package com.booking.football_api.controller;

import com.booking.football_api.dto.BookingResponseDTO;
import com.booking.football_api.dto.CancelBookingRequestDTO;
import com.booking.football_api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<BookingResponseDTO>> getUserBookings(@PathVariable Long userId) {
		// Validate userId
		if (userId == null || userId <= 0) {
			return ResponseEntity.badRequest().build();
		}
		
		try {
			List<BookingResponseDTO> bookings = bookingService.getUserBookings(userId);
			return ResponseEntity.ok(bookings);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PutMapping("/{bookingId}/cancel")
	public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId,
			@Valid @RequestBody CancelBookingRequestDTO request) {
		
		// Validate bookingId
		if (bookingId == null || bookingId <= 0) {
			return ResponseEntity.badRequest().body("ID đơn đặt không hợp lệ");
		}
		
		// Validate request
		if (request == null) {
			return ResponseEntity.badRequest().body("Dữ liệu yêu cầu không hợp lệ");
		}
		
		try {
			boolean success = bookingService.cancelBooking(bookingId, request.getReason());
			if (success) {
				return ResponseEntity.ok("Hủy đơn thành công");
			} else {
				return ResponseEntity.status(422).body("Hủy thất bại: không đủ 24h hoặc trạng thái không cho phép");
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Lỗi khi hủy đơn: " + e.getMessage());
		}
	}
	
}
