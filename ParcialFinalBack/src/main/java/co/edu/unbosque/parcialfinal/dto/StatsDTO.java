package co.edu.unbosque.parcialfinal.dto;

import java.util.Map;

public record StatsDTO(
    long totalUsers, long enabledUsers, long admins, long auditEntries, Map<String, Long> auditByAction) {}
