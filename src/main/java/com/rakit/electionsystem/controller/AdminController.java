package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VoterService voterService;

    @PatchMapping("/voters/{voterId}/activate")
    public ResponseEntity<String> activateVoter(@PathVariable Long voterId) {
        voterService.activateVoter(voterId);
        return ResponseEntity.ok("Voter with ID " + voterId + " has been activated.");
    }

    @PatchMapping("/voters/{voterId}/deactivate")
    public ResponseEntity<String> deactivateVoter(@PathVariable Long voterId) {
        voterService.deactivateVoter(voterId);
        return ResponseEntity.ok("Voter with ID " + voterId + " has been deactivated.");
    }
}
