package co.edu.unbosque.parcialfinal.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.unbosque.parcialfinal.entity.Rol;
import co.edu.unbosque.parcialfinal.entity.Usuario;
import co.edu.unbosque.parcialfinal.repository.UsuarioRepository;
import co.edu.unbosque.parcialfinal.util.AESUtil;

@Component
public class LoadDataBase implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;
	private final AESUtil aesUtil;

	@Value("${app.admin.username}")
	private String adminUsername;

	@Value("${app.admin.password}")
	private String adminPassword;

	public LoadDataBase(UsuarioRepository usuarioRepository, AESUtil aesUtil) {
		this.usuarioRepository = usuarioRepository;
		this.aesUtil = aesUtil;
	}

	@Override
	public void run(String[] args) {

		if (!usuarioRepository.existsByUsername(adminUsername)) {

			Usuario administrador = new Usuario();

			administrador.setUsername(adminUsername);
			administrador.setNombre("Administrador");
			administrador.setApellido("Sistema");
			administrador.setPassword(aesUtil.cifrar(adminPassword));
			administrador.setRole(Rol.ADMIN);

			usuarioRepository.save(administrador);
		}
	}
}