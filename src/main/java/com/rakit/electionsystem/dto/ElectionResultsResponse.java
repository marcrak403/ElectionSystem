package com.rakit.electionsystem.dto;

import com.rakit.electionsystem.model.ElectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for complete election results including all options and their vote counts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElectionResultsResponse {
    private Long electionId;
    private String name;
    private String description;
    private ElectionStatus status;
    private Long totalVotes;
    private List<OptionResultResponse> optionResults;
}
