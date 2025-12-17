package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.VoteRequest;
import com.rakit.electionsystem.service.VoteService;
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
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/cast")
    public ResponseEntity<String> castVote(@RequestBody VoteRequest voteRequest, @AuthenticationPrincipal UserDetails currentUser) {
        voteService.castVote(voteRequest, currentUser);
        return ResponseEntity.ok("Vote cast successfully.");
    }
}
