package co.edu.unbosque.parcialfinal.exception;

public class CantidadNotValidException extends Exception {

	private static final long serialVersionUID = 1L;

	public CantidadNotValidException() {
		super("La cantidad no puede ser negativa");
	}
}
