package com.rakit.electionsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for individual option results within an election.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionResultResponse {
    private Long optionId;
    private String optionTitle;
    private String description;
    private Long voteCount;
}
