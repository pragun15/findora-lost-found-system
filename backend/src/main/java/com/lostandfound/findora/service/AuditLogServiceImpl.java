package com.lostandfound.findora.service;

import com.lostandfound.findora.model.AuditLog;
import com.lostandfound.findora.model.User;
import com.lostandfound.findora.repository.AuditLogRepository;
import com.lostandfound.findora.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void log(Integer userId, String action, String entityName, Integer entityId) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setTimestamp(LocalDateTime.now());

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            log.setUser(user);
        }

        auditLogRepository.save(log);
    }

    @Override
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    public Page<AuditLog> getLogsByUser(Integer userId, Pageable pageable) {
        return auditLogRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<AuditLog> getLogsByEntity(String entityName, Integer entityId, Pageable pageable) {
        return auditLogRepository.findAllByEntityNameAndEntityId(entityName, entityId, pageable);
    }
}
