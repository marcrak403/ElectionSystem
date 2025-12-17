package com.rakit.electionsystem.dto;

import com.rakit.electionsystem.model.ElectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for election statistics including participation metrics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectionStatisticsResponse {
    private Long electionId;
    private String name;
    private ElectionStatus status;
    private Long totalVotes;
    private Long totalParticipants;
    private Long totalOptions;
    private Double participationRate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
