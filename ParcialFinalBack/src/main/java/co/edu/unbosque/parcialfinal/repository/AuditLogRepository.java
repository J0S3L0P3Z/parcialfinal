package co.edu.unbosque.parcialfinal.repository;

import co.edu.unbosque.parcialfinal.model.AuditAction;
import co.edu.unbosque.parcialfinal.model.AuditLog;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

  public Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

  public Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  public Page<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action, Pageable pageable);

  @Query("select a.action, count(a) from AuditLog a group by a.action order by count(a) desc")
  public List<Object[]> countByAction();
}
