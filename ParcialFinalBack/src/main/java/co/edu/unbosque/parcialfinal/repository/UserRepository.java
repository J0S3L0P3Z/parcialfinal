package co.edu.unbosque.parcialfinal.repository;

import co.edu.unbosque.parcialfinal.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  public Optional<User> findByUsername(String username);

  public Optional<User> findByEmail(String email);

  public boolean existsByUsername(String username);

  public void deleteByUsername(String username);

  public long countByEnabledTrue();

  public long countByRole(User.Role role);
}
