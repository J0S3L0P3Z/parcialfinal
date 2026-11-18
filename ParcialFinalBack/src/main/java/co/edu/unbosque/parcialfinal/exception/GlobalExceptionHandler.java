package co.edu.unbosque.parcialfinal.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CredencialesInvalidasException.class)
	public ResponseEntity<Map<String, String>> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {

		Map<String, String> respuesta = new HashMap<>();
		respuesta.put("mensaje", ex.getMessage());

		return new ResponseEntity<>(respuesta, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(TokenInvalidoException.class)
	public ResponseEntity<Map<String, String>> manejarTokenInvalido(TokenInvalidoException ex) {

		Map<String, String> respuesta = new HashMap<>();
		respuesta.put("mensaje", ex.getMessage());

		return new ResponseEntity<>(respuesta, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(DatoInvalidoException.class)
	public ResponseEntity<Map<String, String>> manejarDatoInvalido(DatoInvalidoException ex) {

		Map<String, String> respuesta = new HashMap<>();
		respuesta.put("mensaje", ex.getMessage());

		return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(PasswordNotValidException.class)
	public ResponseEntity<Map<String, String>> manejarPasswordInvalido(PasswordNotValidException ex) {

		Map<String, String> respuesta = new HashMap<>();
		respuesta.put("mensaje", ex.getMessage());

		return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UsuarioYaExisteException.class)
	public ResponseEntity<Map<String, String>> manejarUsuarioYaExiste(UsuarioYaExisteException ex) {

		Map<String, String> respuesta = new HashMap<>();
		respuesta.put("mensaje", ex.getMessage());

		return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
	}
}