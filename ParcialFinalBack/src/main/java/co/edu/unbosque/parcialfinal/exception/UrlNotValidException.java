package co.edu.unbosque.parcialfinal.exception;

public class UrlNotValidException extends Exception {

	private static final long serialVersionUID = 1L;

	public UrlNotValidException() {
		super("La URL de la imagen es obligatoria y debe empezar por http:// o https://");
	}
}
