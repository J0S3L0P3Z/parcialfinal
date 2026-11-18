package co.edu.unbosque.parcialfinal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.parcialfinal.dto.UserDTO;
import co.edu.unbosque.parcialfinal.model.User;
import co.edu.unbosque.parcialfinal.model.User.Role;
import co.edu.unbosque.parcialfinal.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserService() {
	}

	public int create(UserDTO data) {
		if (findUsernameAlreadyTaken(data.getUsername())) {
			return 1;
		}
		userRepo.save(new User(data.getUsername(), passwordEncoder.encode(data.getPassword()), Role.USER));
		return 0;
	}

	public boolean findUsernameAlreadyTaken(String username) {
		return userRepo.findByUsername(username).isPresent();
	}
}
