package com.lostandfound.findora.repository;

import com.lostandfound.findora.model.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Integer> {

    // Claims on a particular item (reporter reviews these)
    List<Claim> findByItemId(Integer itemId);

    // Claims submitted by a user
    List<Claim> findByClaimantId(Integer claimantId);

    // Paginated variants (useful for larger datasets)
    Page<Claim> findByItemId(Integer itemId, Pageable pageable);

    Page<Claim> findByClaimantId(Integer claimantId, Pageable pageable);

    // Used to prevent duplicate claims for the same item by same user
    boolean existsByItemIdAndClaimantId(Integer itemId, Integer claimantId);
}
