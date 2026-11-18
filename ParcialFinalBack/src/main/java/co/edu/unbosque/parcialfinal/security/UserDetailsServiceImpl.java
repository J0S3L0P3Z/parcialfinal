package co.edu.unbosque.parcialfinal.security;

import co.edu.unbosque.parcialfinal.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String identifier = username == null ? "" : username.trim();
    return userRepository
        .findByUsername(identifier)
        .or(() -> userRepository.findByEmail(identifier.toLowerCase(Locale.ROOT)))
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + identifier));
  }
}
