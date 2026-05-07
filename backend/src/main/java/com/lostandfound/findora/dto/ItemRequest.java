package com.lostandfound.findora.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be 3-200 characters")
    private String title;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Category is required")
    private Integer categoryId;

    private String color;

    private String keywords;

    @NotBlank(message = "Status must be LOST or FOUND")
    @Pattern(regexp = "LOST|FOUND", message = "Status must be LOST or FOUND")
    private String status;

    private String location;

    @NotNull(message = "Reporter ID is required")
    private Integer reporterId;

    @NotNull(message = "Date reported is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate dateReported;
}
