package co.edu.unbosque.parcialfinal.model;

import java.util.Objects;

public class Lacteo {

	private Long id;
	private String nombre;
	private int cantidad;
	private Long precio;
	private String url;

	public Lacteo() {
		// TODO Auto-generated constructor stub
	}

	public Lacteo(Long id, String nombre, int cantidad, Long precio, String url) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.precio = precio;
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public Long getPrecio() {
		return precio;
	}

	public void setPrecio(Long precio) {
		this.precio = precio;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Integer.valueOf(cantidad), id, nombre, precio, url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lacteo other = (Lacteo) obj;
		return cantidad == other.cantidad && Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(precio, other.precio) && Objects.equals(url, other.url);
	}

	@Override
	public String toString() {
		return "Lacteo [id=" + id + ", nombre=" + nombre + ", cantidad=" + cantidad + ", precio=" + precio + ", url="
				+ url + "]";
	}

}
