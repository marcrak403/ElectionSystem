package com.rakit.electionsystem.dto;

import com.rakit.electionsystem.model.ElectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectionResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ElectionStatus status;
    private List<ElectionOptionResponse> options;
}
