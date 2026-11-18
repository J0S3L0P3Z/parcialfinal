package co.edu.unbosque.parcialfinal.exception;

public class CredencialesInvalidasException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CredencialesInvalidasException() {
		super("El correo o la contraseña son incorrectos.");
	}
}