package co.edu.unbosque.parcialfinal.dto;

import co.edu.unbosque.parcialfinal.model.AuditAction;
import co.edu.unbosque.parcialfinal.model.AuditLog;
import java.time.Instant;

public record AuditLogDTO(
    Long id, Long userId, String username, AuditAction action, String detail, String ip, Instant createdAt) {

  public static AuditLogDTO from(AuditLog a) {
    return new AuditLogDTO(
        a.getId(), a.getUserId(), a.getUsername(), a.getAction(), a.getDetail(), a.getIp(), a.getCreatedAt());
  }
}
