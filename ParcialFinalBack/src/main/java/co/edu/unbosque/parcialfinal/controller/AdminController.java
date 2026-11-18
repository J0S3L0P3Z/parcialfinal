package co.edu.unbosque.parcialfinal.controller;

import co.edu.unbosque.parcialfinal.dto.AuditLogDTO;
import co.edu.unbosque.parcialfinal.dto.StatsDTO;
import co.edu.unbosque.parcialfinal.model.AuditAction;
import co.edu.unbosque.parcialfinal.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administración", description = "Auditoría y estadísticas (ADMIN)")
public class AdminController {

  private final AuditService auditService;

  public AdminController(AuditService auditService) {
    this.auditService = auditService;
  }

  @Operation(summary = "Auditoría", description = "Movimientos de los usuarios, del más reciente al más antiguo.")
  @GetMapping("/audit")
  public Page<AuditLogDTO> audit(
      @Parameter(description = "Filtrar por id de usuario") @RequestParam(required = false) Long userId,
      @Parameter(description = "Filtrar por acción") @RequestParam(required = false) AuditAction action,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return auditService.find(userId, action, PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 100))));
  }

  @Operation(summary = "Estadísticas")
  @GetMapping("/stats")
  public StatsDTO stats() {
    return auditService.stats();
  }
}
