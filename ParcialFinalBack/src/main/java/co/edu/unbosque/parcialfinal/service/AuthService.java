package co.edu.unbosque.parcialfinal.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.parcialfinal.dto.UsuarioDTO;
import co.edu.unbosque.parcialfinal.entity.Rol;
import co.edu.unbosque.parcialfinal.entity.Usuario;
import co.edu.unbosque.parcialfinal.exception.CredencialesInvalidasException;
import co.edu.unbosque.parcialfinal.exception.DatoInvalidoException;
import co.edu.unbosque.parcialfinal.exception.PasswordNotValidException;
import co.edu.unbosque.parcialfinal.exception.UsuarioYaExisteException;
import co.edu.unbosque.parcialfinal.repository.UsuarioRepository;
import co.edu.unbosque.parcialfinal.security.JwtUtil;
import co.edu.unbosque.parcialfinal.util.AESUtil;

@Service
@Transactional
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final AESUtil aesUtil;
	private final JwtUtil jwtUtil;

	public AuthService(UsuarioRepository usuarioRepository, AESUtil aesUtil, JwtUtil jwtUtil) {
		this.usuarioRepository = usuarioRepository;
		this.aesUtil = aesUtil;
		this.jwtUtil = jwtUtil;
	}

	public Map<String, Object> registrar(Usuario usuario) {

		if (usuario == null) {
			throw new DatoInvalidoException("Los datos del usuario son obligatorios.");
		}

		if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
			throw new DatoInvalidoException("El correo es obligatorio.");
		}

		if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
			throw new DatoInvalidoException("El nombre es obligatorio.");
		}

		if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
			throw new DatoInvalidoException("El apellido es obligatorio.");
		}

		if (usuario.getPassword() == null
				|| !usuario.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$")) {
			throw new PasswordNotValidException();
		}

		if (usuarioRepository.existsByUsername(usuario.getUsername())) {
			throw new UsuarioYaExisteException();
		}

		String passwordCifrada = aesUtil.cifrar(usuario.getPassword());

		usuario.setPassword(passwordCifrada);
		usuario.setRole(Rol.USER);
		usuario.setEnabled(true);
		usuario.setActivo(true);
		usuario.setAccountNonExpired(true);
		usuario.setAccountNonLocked(true);
		usuario.setCredentialsNonExpired(true);

		Usuario usuarioGuardado = usuarioRepository.save(usuario);

		Map<String, Object> respuesta = new HashMap<>();

		respuesta.put("estado", "EXITO");
		respuesta.put("mensaje", "Usuario registrado correctamente.");
		respuesta.put("usuario", convertirDTO(usuarioGuardado));

		return respuesta;
	}

	public Map<String, Object> iniciarSesion(String username, String password) {

		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			throw new DatoInvalidoException("El correo y la contraseña son obligatorios.");
		}

		Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(CredencialesInvalidasException::new);

		if (!usuario.isEnabled() || !usuario.isAccountNonExpired() || !usuario.isAccountNonLocked()
				|| !usuario.isCredentialsNonExpired()) {
			throw new CredencialesInvalidasException();
		}

		String passwordOriginal;

		try {
			passwordOriginal = aesUtil.descifrar(usuario.getPassword());
		} catch (RuntimeException e) {
			throw new CredencialesInvalidasException();
		}

		if (!passwordOriginal.equals(password)) {
			throw new CredencialesInvalidasException();
		}

		String token = jwtUtil.generateToken(usuario);

		Map<String, Object> respuesta = new HashMap<>();

		respuesta.put("estado", "EXITO");
		respuesta.put("mensaje", "Inicio de sesión exitoso.");
		respuesta.put("token", token);
		respuesta.put("usuario", convertirDTO(usuario));

		return respuesta;
	}

	private UsuarioDTO convertirDTO(Usuario usuario) {

		return new UsuarioDTO(usuario.getId(), usuario.getUsername(), usuario.getNombre(), usuario.getApellido(),
				usuario.getRole().name(), usuario.getFechaRegistro(), usuario.getEnabled(), usuario.getActivo(),
				usuario.isAccountNonExpired(), usuario.isAccountNonLocked(), usuario.isCredentialsNonExpired());
	}
}
