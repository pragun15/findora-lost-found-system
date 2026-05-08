package com.lostandfound.findora.service;

public interface AuditLogService {

    void log(Integer userId, String action, String entityName, Integer entityId);
}
