package co.edu.unbosque.parcialfinal.model;

import co.edu.unbosque.parcialfinal.util.EncryptedConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(length = 255)
  private String username;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuditAction action;

  @Column(length = 600)
  private String detail;

  @Convert(converter = EncryptedConverter.class)
  @Column(length = 255)
  private String ip;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  public AuditLog() {}

  public AuditLog(Long userId, String username, AuditAction action, String detail, String ip) {
    this.userId = userId;
    this.username = username;
    this.action = action;
    this.detail = detail;
    this.ip = ip;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public AuditAction getAction() {
    return action;
  }

  public String getDetail() {
    return detail;
  }

  public String getIp() {
    return ip;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
