package com.lostandfound.findora.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {

    @NotNull(message = "Item ID is required")
    private Integer itemId;

    @NotNull(message = "Claimant ID is required")
    private Integer claimantId;

    @Size(max = 2000, message = "Proof text must be at most 2000 characters")
    private String proofText;
}
