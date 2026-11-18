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

import co.edu.unbosque.parcialfinal.dto.LacteoDTO;
import co.edu.unbosque.parcialfinal.exception.CantidadNotValidException;
import co.edu.unbosque.parcialfinal.exception.NombreNotValidException;
import co.edu.unbosque.parcialfinal.exception.PrecioNotValidException;
import co.edu.unbosque.parcialfinal.exception.UrlNotValidException;
import co.edu.unbosque.parcialfinal.service.LacteoService;
import co.edu.unbosque.parcialfinal.util.ExceptionChecker;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/lacteo")
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:5173", "http://localhost:3000",
		"http://localhost:8081" })
@Transactional
@Tag(name = "Lacteos", description = "CRUD de lacteos (ADMIN), listar y comprar (USER)")
@SecurityRequirement(name = "bearerAuth")
public class LacteoController {

	@Autowired
	private LacteoService lacteoServ;

	public LacteoController() {
	}


	@PostMapping(path = "/createjson", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> create(@RequestBody LacteoDTO newLacteo) {
		try {
			ExceptionChecker.checkProducto(newLacteo.getNombre(), newLacteo.getCantidad(), newLacteo.getPrecio(),
					newLacteo.getUrlImagen());
		} catch (NombreNotValidException | CantidadNotValidException | PrecioNotValidException
				| UrlNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		lacteoServ.create(newLacteo);
		return new ResponseEntity<>("Lacteo creado exitosamente", HttpStatus.CREATED);
	}

	@GetMapping("/getall")
	ResponseEntity<List<LacteoDTO>> getAll() {
		return new ResponseEntity<>(lacteoServ.getAll(), HttpStatus.OK);
	}

	@GetMapping("/getbyid/{id}")
	ResponseEntity<LacteoDTO> getById(@PathVariable Long id) {
		LacteoDTO found = lacteoServ.getById(id);
		if (found != null) {
			return new ResponseEntity<>(found, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping(path = "/updatejson/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> update(@PathVariable Long id, @RequestBody LacteoDTO newData) {
		try {
			ExceptionChecker.checkProducto(newData.getNombre(), newData.getCantidad(), newData.getPrecio(),
					newData.getUrlImagen());
		} catch (NombreNotValidException | CantidadNotValidException | PrecioNotValidException
				| UrlNotValidException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		int status = lacteoServ.updateById(id, newData);
		if (status == 0) {
			return new ResponseEntity<>("Lacteo actualizado exitosamente", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Lacteo no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/deletebyid/{id}")
	ResponseEntity<String> deleteById(@PathVariable Long id) {
		int status = lacteoServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<>("Lacteo eliminado exitosamente", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Lacteo no encontrado", HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/comprar/{id}")
	ResponseEntity<String> comprar(@PathVariable Long id, @RequestParam int cantidad) {
		int status = lacteoServ.comprar(id, cantidad);
		if (status == 0) {
			return new ResponseEntity<>("Compra realizada exitosamente", HttpStatus.OK);
		} else if (status == 1) {
			return new ResponseEntity<>("Lacteo no encontrado", HttpStatus.NOT_FOUND);
		} else if (status == 2) {
			return new ResponseEntity<>("No hay suficiente cantidad disponible", HttpStatus.CONFLICT);
		} else {
			return new ResponseEntity<>("La cantidad a comprar debe ser mayor a 0", HttpStatus.BAD_REQUEST);
		}
	}
}
