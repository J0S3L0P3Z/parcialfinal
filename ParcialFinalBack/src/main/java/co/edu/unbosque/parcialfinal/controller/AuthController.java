package co.edu.unbosque.parcialfinal.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.parcialfinal.entity.Usuario;
import co.edu.unbosque.parcialfinal.service.AuthService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/registro")
	public ResponseEntity<Map<String, Object>> registrar(@RequestBody Usuario usuario) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(usuario));
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> iniciarSesion(@RequestBody Map<String, String> datos) {

		String username = datos.get("username");
		String password = datos.get("password");

		return ResponseEntity.ok(authService.iniciarSesion(username, password));
	}
}