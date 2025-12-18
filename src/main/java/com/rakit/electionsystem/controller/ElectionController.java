package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.ElectionOptionRequest;
import com.rakit.electionsystem.dto.ElectionRequest;
import com.rakit.electionsystem.dto.ElectionResponse;
import com.rakit.electionsystem.dto.ErrorResponse;
import com.rakit.electionsystem.model.ElectionStatus;
import com.rakit.electionsystem.service.ElectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Controller for election management endpoints.
 * Handles CRUD operations for elections and election options.
 */
@RestController
@RequestMapping("/api/elections")
@RequiredArgsConstructor
@Tag(name = "Elections", description = "Election management endpoints. Public read access, admin-only write operations.")
public class ElectionController {

    private final ElectionService electionService;

    /**
     * Creates a new election.
     * Admin only - requires ROLE_ADMIN authority.
     *
     * @param electionRequest election details
     * @return created election
     */
    @Operation(
        summary = "Create a new election (Admin only)",
        description = "Creates a new election with the specified details. Initial status is DRAFT. Requires admin role.",
        security = @SecurityRequirement(name = "JWT Bearer Token")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Election created successfully",
            content = @Content(schema = @Schema(implementation = ElectionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - admin role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionResponse> createElection(@RequestBody ElectionRequest electionRequest) {
        ElectionResponse createdElection = electionService.createElection(electionRequest);
        return new ResponseEntity<>(createdElection, HttpStatus.CREATED);
    }

    /**
     * Gets all elections.
     * Public endpoint - no authentication required.
     *
     * @return list of all elections
     */
    @Operation(
        summary = "Get all elections",
        description = "Returns a list of all elections with their options. Public endpoint."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Elections retrieved successfully",
            content = @Content(schema = @Schema(implementation = ElectionResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<List<ElectionResponse>> getAllElections() {
        List<ElectionResponse> elections = electionService.getAllElections();
        return ResponseEntity.ok(elections);
    }

    /**
     * Gets a specific election by ID.
     * Public endpoint - no authentication required.
     *
     * @param id election ID
     * @return election details
     */
    @Operation(
        summary = "Get election by ID",
        description = "Returns detailed information about a specific election including all voting options. Public endpoint."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Election found",
            content = @Content(schema = @Schema(implementation = ElectionResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Election not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ElectionResponse> getElectionById(
            @Parameter(description = "Election ID", required = true) @PathVariable Long id) {
        ElectionResponse election = electionService.getElectionById(id);
        return ResponseEntity.ok(election);
    }

    /**
     * Updates the status of an election.
     * Admin only - requires ROLE_ADMIN authority.
     *
     * @param id election ID
     * @param status new election status (DRAFT, ACTIVE, CLOSED)
     * @return updated election
     */
    @Operation(
        summary = "Update election status (Admin only)",
        description = "Changes the status of an election. Valid statuses: DRAFT, ACTIVE, CLOSED. Only ACTIVE elections accept votes. Requires admin role.",
        security = @SecurityRequirement(name = "JWT Bearer Token")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Status updated successfully",
            content = @Content(schema = @Schema(implementation = ElectionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status value",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - admin role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Election not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionResponse> updateElectionStatus(
            @Parameter(description = "Election ID", required = true) @PathVariable Long id,
            @Parameter(description = "New election status", required = true, schema = @Schema(implementation = ElectionStatus.class))
            @RequestParam ElectionStatus status) {
        ElectionResponse updatedElection = electionService.updateElectionStatus(id, status);
        return ResponseEntity.ok(updatedElection);
    }

    /**
     * Adds a new voting option to an election.
     * Admin only - requires ROLE_ADMIN authority.
     *
     * @param electionId election ID
     * @param optionRequest option details
     * @return updated election with new option
     */
    @Operation(
        summary = "Add voting option to election (Admin only)",
        description = "Adds a new voting option to an existing election. Voters can choose from these options when casting their vote. Requires admin role.",
        security = @SecurityRequirement(name = "JWT Bearer Token")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Option added successfully",
            content = @Content(schema = @Schema(implementation = ElectionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - admin role required",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Election not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @PostMapping("/{electionId}/options")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionResponse> addOptionToElection(
            @Parameter(description = "Election ID", required = true) @PathVariable Long electionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Voting option details",
                required = true
            )
            @RequestBody ElectionOptionRequest optionRequest) {
        ElectionResponse updatedElection = electionService.addOptionToElection(electionId, optionRequest);
        return ResponseEntity.ok(updatedElection);
    }
}
