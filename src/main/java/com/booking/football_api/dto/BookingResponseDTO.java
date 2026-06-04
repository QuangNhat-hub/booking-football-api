package com.booking.football_api.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private String pitchName;
    private String pitchAddress;
    private LocalDateTime startTime;
    private Integer hours;
    private Double totalPrice;
    private String status;
    private String cancelReason;
    
}
