package co.edu.unbosque.parcialfinal.exception;

public class UsuarioYaExisteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UsuarioYaExisteException() {
		super("El usuario ya se encuentra registrado.");
	}
}