package co.edu.unbosque.parcialfinal.exception;

public class PrecioNotValidException extends Exception {

	private static final long serialVersionUID = 1L;

	public PrecioNotValidException() {
		super("El precio debe ser mayor a 0");
	}
}
