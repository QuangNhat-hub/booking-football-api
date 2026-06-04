package com.booking.football_api.dto;

import java.time.LocalDateTime;

public class BookingResponseDTO {
	private Long id;
	private String pitchName;
	private String pitchAddress;
	private LocalDateTime startTime;
	private Integer hours;
	private Double totalPrice;
	private String status;
	private String cancelReason;

	public BookingResponseDTO(Long id, String pitchName, String pitchAddress,
							  LocalDateTime startTime, Integer hours,
							  Double totalPrice, String status, String cancelReason) {
		this.id = id;
		this.pitchName = pitchName;
		this.pitchAddress = pitchAddress;
		this.startTime = startTime;
		this.hours = hours;
		this.totalPrice = totalPrice;
		this.status = status;
		this.cancelReason = cancelReason;
	}

	// Getters
	public Long getId() {
		return id;
	}
	public String getPitchName() {
		return pitchName;
	}
	public String getPitchAddress() {
		return pitchAddress;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public Integer getHours() {
		return hours;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public String getStatus() {
		return status;
	}
	public String getCancelReason() {
		return cancelReason;
	}
}
