package co.edu.unbosque.parcialfinal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.unbosque.parcialfinal.dto.LacteoDTO;
import co.edu.unbosque.parcialfinal.model.Lacteo;
import co.edu.unbosque.parcialfinal.repository.LacteoRepository;

@Service
public class LacteoService implements CRUDOperation<LacteoDTO> {

	@Autowired
	private LacteoRepository lacteoRepo;

	@Autowired
	private ModelMapper modelMapper;

	public LacteoService() {
	}

	@Override
	public int create(LacteoDTO data) {
		Lacteo entity = modelMapper.map(data, Lacteo.class);
		entity.setId(null);
		lacteoRepo.save(entity);
		return 0;
	}

	@Override
	public List<LacteoDTO> getAll() {
		List<LacteoDTO> dtoList = new ArrayList<>();
		lacteoRepo.findAll().forEach(entity -> dtoList.add(modelMapper.map(entity, LacteoDTO.class)));
		return dtoList;
	}

	public LacteoDTO getById(Long id) {
		Optional<Lacteo> found = lacteoRepo.findById(id);
		if (found.isPresent()) {
			return modelMapper.map(found.get(), LacteoDTO.class);
		} else {
			return null;
		}
	}

	@Override
	public int deleteById(Long id) {
		Optional<Lacteo> found = lacteoRepo.findById(id);
		if (found.isPresent()) {
			lacteoRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int updateById(Long id, LacteoDTO newData) {
		Optional<Lacteo> found = lacteoRepo.findById(id);
		if (found.isPresent()) {
			Lacteo temp = found.get();
			temp.setNombre(newData.getNombre());
			temp.setCantidad(newData.getCantidad());
			temp.setPrecio(newData.getPrecio());
			temp.setUrlImagen(newData.getUrlImagen());
			lacteoRepo.save(temp);
			return 0;
		} else {
			return 1;
		}
	}

	public int comprar(Long id, int cantidad) {
		if (cantidad <= 0) {
			return 3;
		}
		Optional<Lacteo> found = lacteoRepo.findById(id);
		if (!found.isPresent()) {
			return 1;
		}
		Lacteo temp = found.get();
		if (temp.getCantidad() < cantidad) {
			return 2;
		}
		temp.setCantidad(temp.getCantidad() - cantidad);
		lacteoRepo.save(temp);
		return 0;
	}

	@Override
	public long count() {
		return lacteoRepo.count();
	}

	@Override
	public boolean exist(Long id) {
		return lacteoRepo.existsById(id);
	}
}
