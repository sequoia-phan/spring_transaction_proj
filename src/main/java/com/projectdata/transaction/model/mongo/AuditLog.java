package com.projectdata.transaction.model.mongo;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;
    private String action;
    private String userId;
    private Instant timestamp;
    private Map<String, Object> details;
}