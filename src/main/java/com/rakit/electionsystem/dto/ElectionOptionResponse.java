package com.rakit.electionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectionOptionResponse {
    private Long id;
    private String optionTitle;
    private String description;
}
