package co.edu.unbosque.parcialfinal.configuration;

import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.repository.UserRepository;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Value("${app.seed.admin-username:}")
	private String adminUsername;

	@Value("${app.seed.admin-password:}")
	private String adminPassword;

	@Value("${app.seed.user-username:}")
	private String userUsername;

	@Value("${app.seed.user-password:}")
	private String userPassword;

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			createIfMissing(userRepo, passwordEncoder, adminUsername, adminPassword, User.Role.ADMIN);
			createIfMissing(userRepo, passwordEncoder, userUsername, userPassword, User.Role.USER);
		};
	}

	private void createIfMissing(UserRepository repo, PasswordEncoder encoder, String username, String password,
			User.Role role) {
		if (isBlank(username) || isBlank(password))
			return;
		String name = username.trim();
		if (repo.findByUsername(name).isPresent()) {
			log.info("El usuario {} ya existe, omitiendo su creación...", name);
			return;
		}
		User user = new User(name, encoder.encode(password), role);
		repo.save(user);
		log.info("Precargando usuario {} con rol {}", name, role);
	}

	private static boolean isBlank(String s) {
		return s == null || s.isBlank();
	}
}
