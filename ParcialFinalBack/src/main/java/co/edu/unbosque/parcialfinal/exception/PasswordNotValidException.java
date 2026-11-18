package co.edu.unbosque.parcialfinal.exception;

public class PasswordNotValidException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PasswordNotValidException() {
		super("La contraseña debe tener mínimo 8 caracteres, " + "una letra minúscula, una mayúscula, "
				+ "un número y un símbolo.");
	}
}