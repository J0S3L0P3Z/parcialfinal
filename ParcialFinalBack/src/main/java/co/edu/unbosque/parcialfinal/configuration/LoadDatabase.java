package co.edu.unbosque.parcialfinal.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.parcialfinal.model.Carne;
import co.edu.unbosque.parcialfinal.model.Lacteo;
import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.model.User.Role;
import co.edu.unbosque.parcialfinal.repository.CarneRepository;
import co.edu.unbosque.parcialfinal.repository.LacteoRepository;
import co.edu.unbosque.parcialfinal.repository.UserRepository;

@Configuration
public class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, LacteoRepository lacteoRepo, CarneRepository carneRepo,
			PasswordEncoder passwordEncoder) {

		return args -> {
			if (userRepo.findByUsername("admin").isPresent()) {
				log.info("Admin ya existe, no se crea ...");
			} else {
				userRepo.save(new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN));
				log.info("Precargando usuario admin (vendedor)");
			}

			if (userRepo.findByUsername("cliente").isPresent()) {
				log.info("Cliente ya existe, no se crea ...");
			} else {
				userRepo.save(new User("cliente", passwordEncoder.encode("cliente123"), Role.USER));
				log.info("Precargando usuario cliente");
			}

			if (lacteoRepo.count() == 0) {
				lacteoRepo.save(new Lacteo("Leche entera 1L", 30, 4500,
						"https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400"));
				lacteoRepo.save(new Lacteo("Queso campesino 500g", 15, 12000,
						"https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=400"));
				log.info("Precargando lacteos");
			}

			if (carneRepo.count() == 0) {
				carneRepo.save(new Carne("Carne de res 1kg", 12, 32000,
						"https://images.unsplash.com/photo-1603048297172-c92544798d5a?w=400"));
				carneRepo.save(new Carne("Pechuga de pollo 1kg", 18, 18000,
						"https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400"));
				log.info("Precargando carnes");
			}
		};
	}
}
