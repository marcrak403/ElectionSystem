package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.ElectionOptionRequest;
import com.rakit.electionsystem.dto.ElectionRequest;
import com.rakit.electionsystem.dto.ElectionResponse;
import com.rakit.electionsystem.model.ElectionStatus;
import com.rakit.electionsystem.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionResponse> createElection(@RequestBody ElectionRequest electionRequest) {
        ElectionResponse createdElection = electionService.createElection(electionRequest);
        return new ResponseEntity<>(createdElection, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ElectionResponse>> getAllElections() {
        List<ElectionResponse> elections = electionService.getAllElections();
        return ResponseEntity.ok(elections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElectionResponse> getElectionById(@PathVariable Long id) {
        ElectionResponse election = electionService.getElectionById(id);
        return ResponseEntity.ok(election);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionResponse> updateElectionStatus(@PathVariable Long id, @RequestParam ElectionStatus status) {
        ElectionResponse updatedElection = electionService.updateElectionStatus(id, status);
        return ResponseEntity.ok(updatedElection);
    }

    @PostMapping("/{electionId}/options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionResponse> addOptionToElection(@PathVariable Long electionId, @RequestBody ElectionOptionRequest optionRequest) {
        ElectionResponse updatedElection = electionService.addOptionToElection(electionId, optionRequest);
        return ResponseEntity.ok(updatedElection);
    }
}
