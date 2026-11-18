package co.edu.unbosque.parcialfinal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unbosque.parcialfinal.entity.Rol;
import co.edu.unbosque.parcialfinal.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsername(String username);

	boolean existsByUsername(String username);

	long countByRole(Rol role);
}
