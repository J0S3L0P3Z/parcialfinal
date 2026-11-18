package co.edu.unbosque.parcialfinal.exception;

public class TokenInvalidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TokenInvalidoException() {
		super("El token es inválido o ha expirado.");
	}
}