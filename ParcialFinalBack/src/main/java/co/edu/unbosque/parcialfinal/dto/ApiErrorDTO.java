package co.edu.unbosque.parcialfinal.dto;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;

public record ApiErrorDTO(Instant timestamp, int status, String message, Map<String, String> fieldErrors) {

  public static ApiErrorDTO of(HttpStatus status, String message, Map<String, String> fieldErrors) {
    return new ApiErrorDTO(Instant.now(), status.value(), message, fieldErrors);
  }
}
