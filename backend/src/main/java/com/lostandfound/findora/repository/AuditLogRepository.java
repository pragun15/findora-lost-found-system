package com.lostandfound.findora.repository;

import com.lostandfound.findora.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    Page<AuditLog> findAllByUserId(Integer userId, Pageable pageable);

    Page<AuditLog> findAllByEntityNameAndEntityId(String entityName, Integer entityId, Pageable pageable);
}
