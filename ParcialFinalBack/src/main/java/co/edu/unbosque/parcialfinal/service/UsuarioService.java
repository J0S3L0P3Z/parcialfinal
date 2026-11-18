package co.edu.unbosque.parcialfinal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unbosque.parcialfinal.dto.UsuarioDTO;
import co.edu.unbosque.parcialfinal.entity.Usuario;
import co.edu.unbosque.parcialfinal.entity.Rol;
import co.edu.unbosque.parcialfinal.exception.DatoInvalidoException;
import co.edu.unbosque.parcialfinal.exception.UsuarioYaExisteException;
import co.edu.unbosque.parcialfinal.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public UsuarioDTO buscarPorId(Long id) {

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new DatoInvalidoException("Usuario no encontrado."));

		return convertirDTO(usuario);
	}

	public UsuarioDTO buscarPorUsername(String username) {

		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new DatoInvalidoException("Usuario no encontrado."));

		return convertirDTO(usuario);
	}

	public List<UsuarioDTO> buscarTodos() {

		return usuarioRepository.findAll().stream().map(this::convertirDTO).collect(Collectors.toList());
	}

	public UsuarioDTO crear(UsuarioDTO datos, String passwordCifrada) {

		if (datos == null) {
			throw new DatoInvalidoException("Los datos del usuario son obligatorios.");
		}

		if (datos.getUsername() == null || datos.getUsername().isBlank()) {
			throw new DatoInvalidoException("El correo es obligatorio.");
		}

		if (passwordCifrada == null || passwordCifrada.isBlank()) {
			throw new DatoInvalidoException("La contraseña es obligatoria.");
		}

		if (usuarioRepository.existsByUsername(datos.getUsername())) {
			throw new UsuarioYaExisteException();
		}

		if (datos.getRole() == null || datos.getRole().isBlank()) {
			throw new DatoInvalidoException("El rol es obligatorio.");
		}

		Usuario usuario = new Usuario();

		usuario.setUsername(datos.getUsername());
		usuario.setNombre(datos.getNombre());
		usuario.setApellido(datos.getApellido());
		usuario.setPassword(passwordCifrada);
		usuario.setRole(convertirRol(datos.getRole()));

		return convertirDTO(usuarioRepository.save(usuario));
	}

	public UsuarioDTO actualizar(Long id, UsuarioDTO datos) {

		if (datos == null) {
			throw new DatoInvalidoException("Los datos del usuario son obligatorios.");
		}

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new DatoInvalidoException("Usuario no encontrado."));

		if (datos.getUsername() != null && !datos.getUsername().equals(usuario.getUsername())) {

			if (usuarioRepository.existsByUsername(datos.getUsername())) {
				throw new UsuarioYaExisteException();
			}

			usuario.setUsername(datos.getUsername());
		}

		if (datos.getNombre() != null) {
			usuario.setNombre(datos.getNombre());
		}

		if (datos.getApellido() != null) {
			usuario.setApellido(datos.getApellido());
		}

		if (datos.getRole() != null) {
			usuario.setRole(convertirRol(datos.getRole()));
		}

		if (datos.getEnabled() != null) {
			usuario.setEnabled(datos.getEnabled());
		}

		if (datos.getActivo() != null) {
			usuario.setActivo(datos.getActivo());
		}

		if (datos.getAccountNonExpired() != null) {
			usuario.setAccountNonExpired(datos.getAccountNonExpired());
		}

		if (datos.getAccountNonLocked() != null) {
			usuario.setAccountNonLocked(datos.getAccountNonLocked());
		}

		if (datos.getCredentialsNonExpired() != null) {
			usuario.setCredentialsNonExpired(datos.getCredentialsNonExpired());
		}

		return convertirDTO(usuarioRepository.save(usuario));
	}

	public void eliminar(Long id) {

		if (!usuarioRepository.existsById(id)) {
			throw new DatoInvalidoException("Usuario no encontrado.");
		}

		usuarioRepository.deleteById(id);
	}

	public long contar() {
		return usuarioRepository.count();
	}

	public boolean existePorUsername(String username) {
		return usuarioRepository.existsByUsername(username);
	}

	private Rol convertirRol(String nombreRol) {

		try {
			return Rol.valueOf(nombreRol.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new DatoInvalidoException("El rol debe ser ADMIN o USER.");
		}
	}

	private UsuarioDTO convertirDTO(Usuario usuario) {

		return new UsuarioDTO(usuario.getId(), usuario.getUsername(), usuario.getNombre(), usuario.getApellido(),
				usuario.getRole().name(), usuario.getFechaRegistro(), usuario.getEnabled(), usuario.getActivo(),
				usuario.isAccountNonExpired(), usuario.isAccountNonLocked(), usuario.isCredentialsNonExpired());
	}
}