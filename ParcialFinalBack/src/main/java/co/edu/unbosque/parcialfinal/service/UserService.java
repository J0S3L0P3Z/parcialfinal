package co.edu.unbosque.parcialfinal.service;

import co.edu.unbosque.parcialfinal.dto.UserDTO;
import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.repository.UserRepository;
import co.edu.unbosque.parcialfinal.util.PasswordValidator;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements CRUDOperation<UserDTO> {

	private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._-]{3,30}$");

	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private final UserRepository userRepo;

	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return userRepo.existsById(id);
	}

	@Override
	@Transactional
	public int create(UserDTO data) {
		if (data == null || !validUsername(data.getUsername()) || !validEmail(data.getEmail())) {
			return 4;
		}
		String username = data.getUsername().trim();
		if (userRepo.existsByUsername(username)) {
			return 1;
		}
		if (!PasswordValidator.isValid(data.getPassword())) {
			return 3;
		}
		User entity = new User(username, passwordEncoder.encode(data.getPassword()));
		if (data.getRole() != null) {
			entity.setRole(data.getRole());
		}
		if (data.getEnabled() != null) {
			entity.setEnabled(data.getEnabled());
		}
		userRepo.save(entity);
		return 0;
	}

	@Transactional
	public int register(UserDTO data) {
		if (data != null) {
			data.setRole(User.Role.USER);
			data.setEnabled(true);
		}
		return create(data);
	}

	@Override
	public List<UserDTO> getAll() {
		return userRepo.findAll().stream().map(this::toDTO).toList();
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			userRepo.delete(found.get());
			return 0;
		}
		return 1;
	}

	@Transactional
	public int deleteByUsername(String username) {
		Optional<User> found = userRepo.findByUsername(username);
		if (found.isPresent()) {
			userRepo.delete(found.get());
			return 0;
		}
		return 1;
	}

	@Override
	@Transactional
	public int updateById(Long id, UserDTO newData) {
		Optional<User> found = userRepo.findById(id);
		if (found.isEmpty()) {
			return 2;
		}
		if (newData == null) {
			return 4;
		}
		User user = found.get();
		if (notBlank(newData.getUsername())) {
			String username = newData.getUsername().trim();
			if (!validUsername(username)) {
				return 4;
			}
			Optional<User> other = userRepo.findByUsername(username);
			if (other.isPresent() && !other.get().getId().equals(id)) {
				return 1;
			}
			user.setUsername(username);
		}
		if (notBlank(newData.getEmail())) {
			if (!validEmail(newData.getEmail())) {
				return 4;
			}
			String email = normalizeEmail(newData.getEmail());
			Optional<User> other = userRepo.findByEmail(email);
			if (other.isPresent() && !other.get().getId().equals(id)) {
				return 1;
			}
		}
		if (notBlank(newData.getPassword())) {
			if (!PasswordValidator.isValid(newData.getPassword())) {
				return 3;
			}
			user.setPassword(passwordEncoder.encode(newData.getPassword()));
		}
		if (newData.getRole() != null) {
			user.setRole(newData.getRole());
		}
		if (newData.getEnabled() != null) {
			user.setEnabled(newData.getEnabled());
		}
		userRepo.save(user);
		return 0;
	}

	public UserDTO getById(Long id) {
		return userRepo.findById(id).map(this::toDTO).orElse(null);
	}

	public boolean findUsernameAlreadyTaken(String username) {
		return username != null && userRepo.existsByUsername(username.trim());
	}

	@Transactional
	public void registerLogin(Long id) {
		userRepo.findById(id).ifPresent(u -> u.setLastLoginAt(Instant.now()));
	}

	public UserDTO toDTO(User u) {
		UserDTO dto = new UserDTO();
		dto.setId(u.getId());
		dto.setUsername(u.getUsername());
		dto.setRole(u.getRole());
		dto.setEnabled(u.isEnabled());
		dto.setCreatedAt(u.getCreatedAt());
		dto.setLastLoginAt(u.getLastLoginAt());
		return dto;
	}

	private static boolean validUsername(String username) {
		return username != null && USERNAME.matcher(username.trim()).matches();
	}

	private static boolean validEmail(String email) {
		return email != null && email.length() <= 254 && EMAIL.matcher(email.trim()).matches();
	}

	private static String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private static boolean notBlank(String s) {
		return s != null && !s.isBlank();
	}
}
