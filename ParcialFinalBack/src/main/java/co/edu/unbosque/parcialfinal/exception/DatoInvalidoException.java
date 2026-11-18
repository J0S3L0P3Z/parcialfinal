package co.edu.unbosque.parcialfinal.exception;

public class DatoInvalidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DatoInvalidoException(String mensaje) {
		super(mensaje);
	}
}