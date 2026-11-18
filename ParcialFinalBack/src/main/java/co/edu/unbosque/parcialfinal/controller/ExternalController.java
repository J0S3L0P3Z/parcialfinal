package co.edu.unbosque.parcialfinal.controller;

import co.edu.unbosque.parcialfinal.dto.FactDTO;
import co.edu.unbosque.parcialfinal.model.AuditAction;
import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.service.AuditService;
import co.edu.unbosque.parcialfinal.service.ExternalApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "API externa", description = "Consumo de una API externa desde el back")
public class ExternalController {

  private final ExternalApiService externalApi;

  private final AuditService audit;

  public ExternalController(ExternalApiService externalApi, AuditService audit) {
    this.externalApi = externalApi;
    this.audit = audit;
  }

  @Operation(summary = "Dato curioso aleatorio")
  @GetMapping("/fact/random")
  public ResponseEntity<FactDTO> randomFact(
      @AuthenticationPrincipal User me,
      @Parameter(description = "Idioma: en o de", example = "en") @RequestParam(defaultValue = "en") String language) {
    FactDTO fact = externalApi.randomFact(language);
    audit.log(me.getId(), me.getUsername(), AuditAction.EXTERNAL_API_CALL, "fact/random");
    return ResponseEntity.ok(fact);
  }

  @Operation(summary = "Dato curioso del día")
  @GetMapping("/fact/today")
  public ResponseEntity<FactDTO> todayFact(
      @AuthenticationPrincipal User me,
      @Parameter(description = "Idioma: en o de", example = "en") @RequestParam(defaultValue = "en") String language) {
    FactDTO fact = externalApi.todayFact(language);
    audit.log(me.getId(), me.getUsername(), AuditAction.EXTERNAL_API_CALL, "fact/today");
    return ResponseEntity.ok(fact);
  }
}
