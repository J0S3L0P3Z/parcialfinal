package co.edu.unbosque.parcialfinal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.parcialfinal.dto.CarneDTO;
import co.edu.unbosque.parcialfinal.exception.CantidadNotValidException;
import co.edu.unbosque.parcialfinal.exception.NombreNotValidException;
import co.edu.unbosque.parcialfinal.exception.PrecioNotValidException;
import co.edu.unbosque.parcialfinal.exception.UrlNotValidException;
import co.edu.unbosque.parcialfinal.service.CarneService;
import co.edu.unbosque.parcialfinal.util.ExceptionChecker;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/carne")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000",
		"http://localhost:8081" })
@Transactional
@Tag(name = "Carnes", description = "CRUD de carnes (ADMIN), listar y comprar (USER)")
@SecurityRequirement(name = "bearerAuth")
public class CarneController {

	@Autowired
	private CarneService carneServ;

	public CarneController() {
	}


	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> create(@RequestBody CarneDTO newCarne) {
		try {
			ExceptionChecker.checkProducto(newCarne.getNombre(), newCarne.getCantidad(), newCarne.getPrecio(),
					newCarne.getUrlImagen());
		} catch (NombreNotValidException | CantidadNotValidException | PrecioNotValidException
				| UrlNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		carneServ.create(newCarne);
		return new ResponseEntity<>("Carne creada exitosamente", HttpStatus.CREATED);
	}

	@GetMapping("/getall")
	ResponseEntity<List<CarneDTO>> getAll() {
		return new ResponseEntity<>(carneServ.getAll(), HttpStatus.OK);
	}

	@GetMapping("/getbyid/{id}")
	ResponseEntity<CarneDTO> getById(@PathVariable Long id) {
		CarneDTO found = carneServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(path = "/updatejson/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> update(@PathVariable Long id, @RequestBody CarneDTO newData) {
		try {
			ExceptionChecker.checkProducto(newData.getNombre(), newData.getCantidad(), newData.getPrecio(),
					newData.getUrlImagen());
		} catch (NombreNotValidException | CantidadNotValidException | PrecioNotValidException
				| UrlNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = carneServ.updateById(id, newData);
		if (status == 0) {
			return new ResponseEntity<>("Carne actualizada exitosamente", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Carne no encontrada", HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(@PathVariable Long id) {
		int status = carneServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Carne eliminada exitosamente", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Carne no encontrada", HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/comprar/{id}")
	ResponseEntity<String> comprar(@PathVariable Long id, @RequestParam int cantidad) {
		int status = carneServ.comprar(id, cantidad);
		if (status == 0) {
			return new ResponseEntity<>("Compra realizada exitosamente", HttpStatus.OK);
		} else if (status == 1) {
			return new ResponseEntity<>("Carne no encontrada", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("No hay suficiente cantidad disponible", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>("La cantidad a comprar debe ser mayor a 0", HttpStatus.BAD_REQUEST);
		}
	}
}
