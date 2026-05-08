package com.lostandfound.findora.repository;

import com.lostandfound.findora.model.Item;
import com.lostandfound.findora.model.ItemStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds dynamic filters for Item listing (beginner-friendly).
 */
public class ItemSpecification {

    public static Specification<Item> buildSpec(
            String keyword,
            String status,
            Integer categoryId,
            String color,
            String location,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude soft-deleted items
            predicates.add(cb.isFalse(root.get("isDeleted")));

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), like),
                        cb.like(root.get("description"), like),
                        cb.like(root.get("keywords"), like)
                ));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), ItemStatus.valueOf(status)));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (color != null && !color.isBlank()) {
                predicates.add(cb.equal(root.get("color"), color));
            }

            if (location != null && !location.isBlank()) {
                String like = "%" + location.trim() + "%";
                predicates.add(cb.like(root.get("location"), like));
            }

            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateReported"), dateFrom));
            }

            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateReported"), dateTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
