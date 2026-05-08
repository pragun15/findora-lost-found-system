package com.lostandfound.findora.service;

import com.lostandfound.findora.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {

    void log(Integer userId, String action, String entityName, Integer entityId);

    Page<AuditLog> getAllLogs(Pageable pageable);

    Page<AuditLog> getLogsByUser(Integer userId, Pageable pageable);

    Page<AuditLog> getLogsByEntity(String entityName, Integer entityId, Pageable pageable);
}
