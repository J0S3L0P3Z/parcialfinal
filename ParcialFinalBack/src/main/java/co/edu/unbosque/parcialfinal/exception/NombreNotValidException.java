package co.edu.unbosque.parcialfinal.exception;

public class NombreNotValidException extends Exception {

	private static final long serialVersionUID = 1L;

	public NombreNotValidException() {
		super("El nombre es obligatorio y no puede contener los caracteres < >");
	}
}
