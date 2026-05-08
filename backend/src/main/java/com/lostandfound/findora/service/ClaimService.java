package com.lostandfound.findora.service;

import com.lostandfound.findora.dto.ClaimRequest;
import com.lostandfound.findora.model.Claim;

import java.util.List;

public interface ClaimService {

    Claim submitClaim(ClaimRequest request);

    List<Claim> getClaimsByItem(Integer itemId);

    List<Claim> getClaimsByUser(Integer userId);

    Claim updateClaimStatus(Integer claimId, String status, Integer userId);
}
