package com.lostandfound.findora.service;

import com.lostandfound.findora.dto.ClaimRequest;
import com.lostandfound.findora.model.Claim;
import com.lostandfound.findora.model.ClaimStatus;
import com.lostandfound.findora.model.Item;
import com.lostandfound.findora.model.ItemStatus;
import com.lostandfound.findora.model.User;
import com.lostandfound.findora.repository.ClaimRepository;
import com.lostandfound.findora.repository.ItemRepository;
import com.lostandfound.findora.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public ClaimServiceImpl(ClaimRepository claimRepository,
                            ItemRepository itemRepository,
                            UserRepository userRepository,
                            AuditLogService auditLogService) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public Claim submitClaim(ClaimRequest request) {
        Item item = itemRepository.findByIdAndIsDeletedFalse(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getStatus() != ItemStatus.FOUND) {
            throw new RuntimeException("Only FOUND items can be claimed");
        }

        boolean alreadyClaimed = claimRepository.existsByItemIdAndClaimantId(
                request.getItemId(), request.getClaimantId()
        );
        if (alreadyClaimed) {
            throw new RuntimeException("You already claimed this item");
        }

        User claimant = userRepository.findById(request.getClaimantId())
                .orElseThrow(() -> new RuntimeException("Claimant not found"));

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimant(claimant);
        claim.setProofText(request.getProofText());
        claim.setStatus(ClaimStatus.PENDING);

        Claim saved = claimRepository.save(claim);

        auditLogService.log(claimant.getId(), "CLAIM_SUBMITTED", "claims", saved.getId());

        return saved;
    }

    @Override
    public List<Claim> getClaimsByItem(Integer itemId) {
        return claimRepository.findByItemId(itemId);
    }

    @Override
    public List<Claim> getClaimsByUser(Integer userId) {
        return claimRepository.findByClaimantId(userId);
    }

    @Override
    public Claim updateClaimStatus(Integer claimId, String status, Integer userId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        Item item = claim.getItem();
        if (!item.getReporter().getId().equals(userId)) {
            throw new RuntimeException("Only the reporter can approve/reject claims");
        }

        ClaimStatus newStatus = ClaimStatus.valueOf(status);
        claim.setStatus(newStatus);
        Claim saved = claimRepository.save(claim);

        if (newStatus == ClaimStatus.APPROVED) {
            item.setStatus(ItemStatus.CLAIMED);
            itemRepository.save(item);
            auditLogService.log(userId, "CLAIM_APPROVED", "claims", claimId);
        } else if (newStatus == ClaimStatus.REJECTED) {
            auditLogService.log(userId, "CLAIM_REJECTED", "claims", claimId);
        }

        return saved;
    }
}
