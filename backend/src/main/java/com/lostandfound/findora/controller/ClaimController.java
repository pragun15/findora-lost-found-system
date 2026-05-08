package com.lostandfound.findora.controller;

import com.lostandfound.findora.dto.ClaimRequest;
import com.lostandfound.findora.model.Claim;
import com.lostandfound.findora.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<Claim> submitClaim(@Valid @RequestBody ClaimRequest request) {
        Claim claim = claimService.submitClaim(request);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<Claim>> getClaimsByItem(@PathVariable Integer itemId) {
        return ResponseEntity.ok(claimService.getClaimsByItem(itemId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Claim>> getClaimsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(claimService.getClaimsByUser(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Claim> updateClaimStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request,
            @RequestParam Integer userId
    ) {
        String status = request.get("status");
        Claim updated = claimService.updateClaimStatus(id, status, userId);
        return ResponseEntity.ok(updated);
    }
}
