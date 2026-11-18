package co.edu.unbosque.parcialfinal.exception;

import co.edu.unbosque.parcialfinal.dto.ApiErrorDTO;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorDTO> validation(MethodArgumentNotValidException e) {
    Map<String, String> fields = new LinkedHashMap<>();
    e.getBindingResult().getFieldErrors().forEach(f -> fields.putIfAbsent(f.getField(), f.getDefaultMessage()));
    return ResponseEntity.badRequest()
        .body(ApiErrorDTO.of(HttpStatus.BAD_REQUEST, "Datos inválidos", fields));
  }

  @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
  public ResponseEntity<ApiErrorDTO> badRequest(Exception e) {
    return ResponseEntity.badRequest()
        .body(ApiErrorDTO.of(HttpStatus.BAD_REQUEST, "La solicitud no tiene el formato esperado", null));
  }

  @ExceptionHandler(ExternalApiException.class)
  public ResponseEntity<ApiErrorDTO> external(ExternalApiException e) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(ApiErrorDTO.of(HttpStatus.BAD_GATEWAY, e.getMessage(), null));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiErrorDTO> forbidden(AccessDeniedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiErrorDTO.of(HttpStatus.FORBIDDEN, "No tienes permiso para hacer esto", null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorDTO> unexpected(Exception e) {
    log.error("Error inesperado", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiErrorDTO.of(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado", null));
  }
}
