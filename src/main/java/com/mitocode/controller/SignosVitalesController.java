package com.mitocode.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.dto.SignosVitalesDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.SignosVitales;
import com.mitocode.service.ISignosVitalesService;

@RestController
@RequestMapping("/signosvitales")
public class SignosVitalesController {
	
	@Autowired
	private ISignosVitalesService service;
	
	@Autowired
	private ModelMapper mapper;
	
	@GetMapping
	public ResponseEntity<List<SignosVitalesDTO>> listar() throws Exception{
		List<SignosVitalesDTO> lista = service.listar().stream().map(s -> mapper.map(s, SignosVitalesDTO.class)).
				collect(Collectors.toList());
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<SignosVitalesDTO> listarPorId(@PathVariable("id") Integer id) throws Exception{
		SignosVitales model = service.listarPorId(id);
		if(model == null) {
			throw new ModelNotFoundException("ID no encontrado: "+id);
		}
		SignosVitalesDTO signosDTO = mapper.map(model,SignosVitalesDTO.class);
		return new ResponseEntity<>(signosDTO, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Void> registrar(@Valid @RequestBody SignosVitalesDTO dto )throws Exception{
		System.out.println(dto.getPaciente().getNombres());
		System.out.println(dto.getPulso());
		
		SignosVitales obj = service.registrar(mapper.map(dto, SignosVitales.class));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				       .buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping
	public ResponseEntity<SignosVitalesDTO> modificar(@Valid @RequestBody SignosVitalesDTO dto) throws Exception{
		
		SignosVitales obj = service.listarPorId(dto.getId());
		
		if(obj == null) {
			throw new ModelNotFoundException("Id no encontrado: "+dto.getId());
		}
		SignosVitales s = mapper.map(dto, SignosVitales.class);
		SignosVitales signos = service.registrar(s);
		SignosVitalesDTO signosResponse = mapper.map(signos, SignosVitalesDTO.class);
		return new ResponseEntity<>(signosResponse, HttpStatus.OK);
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception{
		
		SignosVitales obj = service.listarPorId(id);
		if(obj == null) {
			throw new ModelNotFoundException("Id no encontrado: "+id);
		}
		service.eliminar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Page<SignosVitalesDTO>> listarPageable(Pageable page) throws Exception{
		Page<SignosVitalesDTO> signos = service.listarPageable(page).map(s -> mapper.map(s, SignosVitalesDTO.class));
		return new ResponseEntity<>(signos, HttpStatus.OK);
	}
	

}
