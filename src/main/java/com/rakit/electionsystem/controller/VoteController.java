package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.VoteRequest;
import com.rakit.electionsystem.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Tag(name = "Voting", description = "Anonymous voting endpoints")
@SecurityRequirement(name = "JWT Bearer Token")
public class VoteController {

    private final VoteService voteService;

    @Operation(summary = "Cast a vote",
               description = "Casts an anonymous vote in an election. Creates separate Vote and VotingRecord to maintain anonymity.")
    @PostMapping("/cast")
    public ResponseEntity<String> castVote(@RequestBody VoteRequest voteRequest, @AuthenticationPrincipal UserDetails currentUser) {
        voteService.castVote(voteRequest, currentUser);
        return ResponseEntity.ok("Vote cast successfully.");
    }
}
