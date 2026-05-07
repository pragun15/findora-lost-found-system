package com.lostandfound.findora.repository;

import com.lostandfound.findora.model.Item;
import com.lostandfound.findora.model.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Item repository supports:
 * - pagination
 * - excluding soft-deleted items
 * - simple smart-matching queries
 * - dynamic filtering via JPA Specifications
 */
public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {

    Optional<Item> findByIdAndIsDeletedFalse(Integer id);

    Page<Item> findAllByIsDeletedFalse(Pageable pageable);

    List<Item> findByStatusAndIsDeletedFalse(ItemStatus status);

    Page<Item> findByReporterIdAndIsDeletedFalse(Integer reporterId, Pageable pageable);

    /**
     * Smart matching query (beginner-friendly, JPQL):
     * Find FOUND items that might match an input LOST item.
     *
     * Note: scoring/ranking can also be done in service layer later.
     */
    @Query("""
            SELECT i
            FROM Item i
            WHERE i.status = :foundStatus
              AND i.isDeleted = false
              AND (
                    i.category.id = :categoryId
                 OR (:color IS NOT NULL AND i.color = :color)
                 OR (:location IS NOT NULL AND i.location LIKE CONCAT('%', :location, '%'))
                 OR (:keyword IS NOT NULL AND i.keywords LIKE CONCAT('%', :keyword, '%'))
              )
            ORDER BY i.createdAt DESC
            """)
    List<Item> findPotentialMatches(
            @Param("foundStatus") ItemStatus foundStatus,
            @Param("categoryId") Integer categoryId,
            @Param("color") String color,
            @Param("location") String location,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
