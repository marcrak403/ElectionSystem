package com.rakit.electionsystem.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private Long electionId;
    private Long optionId;
}
