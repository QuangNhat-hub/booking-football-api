package com.booking.football_api.controller;

import com.booking.football_api.dto.BookingResponseDTO;
import com.booking.football_api.dto.CancelBookingRequestDTO;
import com.booking.football_api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<BookingResponseDTO>> getUserBookings(@PathVariable Long userId) {
		return ResponseEntity.ok(bookingService.getUserBookings(userId));
	}

	@PutMapping("/{bookingId}/cancel")
	public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId,
			@RequestBody CancelBookingRequestDTO request) {
		boolean success = bookingService.cancelBooking(bookingId, request.getReason());
		if (success) {
			return ResponseEntity.ok("Hủy đơn thành công");
		} else {
			return ResponseEntity.badRequest().body("Hủy thất bại: không đủ 24h hoặc trạng thái không cho phép");
		}
	}
}
