package co.edu.unbosque.parcialfinal.util;

import co.edu.unbosque.parcialfinal.exception.CantidadNotValidException;
import co.edu.unbosque.parcialfinal.exception.NombreNotValidException;
import co.edu.unbosque.parcialfinal.exception.PrecioNotValidException;
import co.edu.unbosque.parcialfinal.exception.UrlNotValidException;

public class ExceptionChecker {

	public static void checkNombre(String nombre) throws NombreNotValidException {
		if (nombre == null || nombre.isBlank() || nombre.contains("<") || nombre.contains(">")) {
			throw new NombreNotValidException();
		}
	}

	public static void checkCantidad(int cantidad) throws CantidadNotValidException {
		if (cantidad < 0) {
			throw new CantidadNotValidException();
		}
	}

	public static void checkPrecio(double precio) throws PrecioNotValidException {
		if (precio <= 0) {
			throw new PrecioNotValidException();
		}
	}

	public static void checkUrl(String url) throws UrlNotValidException {
		if (url == null || !(url.startsWith("http://") || url.startsWith("https://"))) {
			throw new UrlNotValidException();
		}
	}

	public static void checkProducto(String nombre, int cantidad, double precio, String url)
			throws NombreNotValidException, CantidadNotValidException, PrecioNotValidException, UrlNotValidException {
		checkNombre(nombre);
		checkCantidad(cantidad);
		checkPrecio(precio);
		checkUrl(url);
	}
}
