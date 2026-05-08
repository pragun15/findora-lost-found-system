package com.lostandfound.findora.controller;

import com.lostandfound.findora.model.AuditLog;
import com.lostandfound.findora.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) Integer entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        if (userId != null) {
            return ResponseEntity.ok(auditLogService.getLogsByUser(userId, pageable));
        }

        if (entity != null && entityId != null) {
            return ResponseEntity.ok(auditLogService.getLogsByEntity(entity, entityId, pageable));
        }

        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
    }
}
