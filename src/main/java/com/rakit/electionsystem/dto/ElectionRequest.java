package com.rakit.electionsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ElectionRequest {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
