package co.edu.unbosque.parcialfinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.edu.unbosque.parcialfinal.dto.UsuarioDTO;
import co.edu.unbosque.parcialfinal.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> buscarTodos() {
		return ResponseEntity.ok(usuarioService.buscarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<UsuarioDTO> buscarPorUsername(@PathVariable String username) {
		return ResponseEntity.ok(usuarioService.buscarPorUsername(username));
	}

	@PostMapping
	public ResponseEntity<UsuarioDTO> crear(@RequestBody UsuarioDTO datos, @RequestParam String passwordCifrada) {

		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(datos, passwordCifrada));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @RequestBody UsuarioDTO datos) {

		return ResponseEntity.ok(usuarioService.actualizar(id, datos));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		usuarioService.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/contar")
	public ResponseEntity<Long> contar() {
		return ResponseEntity.ok(usuarioService.contar());
	}

	@GetMapping("/existe/{username}")
	public ResponseEntity<Boolean> existePorUsername(@PathVariable String username) {
		return ResponseEntity.ok(usuarioService.existePorUsername(username));
	}
}