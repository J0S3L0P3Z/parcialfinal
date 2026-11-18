package co.edu.unbosque.parcialfinal.service;

import co.edu.unbosque.parcialfinal.dto.AuditLogDTO;
import co.edu.unbosque.parcialfinal.dto.StatsDTO;
import co.edu.unbosque.parcialfinal.model.AuditAction;
import co.edu.unbosque.parcialfinal.model.AuditLog;
import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.repository.AuditLogRepository;
import co.edu.unbosque.parcialfinal.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditService {

  private static final Logger log = LoggerFactory.getLogger(AuditService.class);

  private static final int MAX_DETAIL = 590;

  private final AuditLogRepository repo;

  private final UserRepository users;

  public AuditService(AuditLogRepository repo, UserRepository users) {
    this.repo = repo;
    this.users = users;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void log(Long userId, String username, AuditAction action, String detail) {
    try {
      String d = detail == null ? null : (detail.length() > MAX_DETAIL ? detail.substring(0, MAX_DETAIL) : detail);
      repo.saveAndFlush(new AuditLog(userId, username, action, d, currentIp()));
    } catch (RuntimeException e) {
      log.warn("No se pudo registrar la auditoría {}: {}", action, e.getMessage());
    }
  }

  @Transactional(readOnly = true)
  public Page<AuditLogDTO> find(Long userId, AuditAction action, Pageable pageable) {
    Page<AuditLog> page;
    if (userId != null) {
      page = repo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    } else if (action != null) {
      page = repo.findByActionOrderByCreatedAtDesc(action, pageable);
    } else {
      page = repo.findAllByOrderByCreatedAtDesc(pageable);
    }
    return page.map(AuditLogDTO::from);
  }

  @Transactional(readOnly = true)
  public StatsDTO stats() {
    Map<String, Long> byAction = new LinkedHashMap<>();
    for (Object[] row : repo.countByAction()) {
      byAction.put(String.valueOf(row[0]), (Long) row[1]);
    }
    return new StatsDTO(
        users.count(), users.countByEnabledTrue(), users.countByRole(User.Role.ADMIN), repo.count(), byAction);
  }

  private static String currentIp() {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes sra) {
      return sra.getRequest().getRemoteAddr();
    }
    return null;
  }
}
