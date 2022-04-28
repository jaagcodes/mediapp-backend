package com.mitocode.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

import com.mitocode.dto.ExamenDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Examen;
import com.mitocode.service.IExamenService;

@RestController
@RequestMapping("/examenes")
public class ExamenController {

	@Autowired
	private IExamenService service;

	@Autowired
	private ModelMapper mapper;

	@GetMapping
	// @RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<ExamenDTO>> listar() throws Exception {
		List<ExamenDTO> lista = service.listar().stream().map(p -> mapper.map(p, ExamenDTO.class))
				.collect(Collectors.toList());

		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ExamenDTO> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Examen obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		ExamenDTO dto = mapper.map(obj, ExamenDTO.class);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/*
	 * @PostMapping public ResponseEntity<Examen> registrar(@RequestBody Examen
	 * p) throws Exception { Examen obj = service.registrar(p); return new
	 * ResponseEntity<Examen>(obj, HttpStatus.CREATED); }
	 */

	@PostMapping // Debe devolver la Url para que el que lo consuma tenga más información
	public ResponseEntity<Void> registrar(@Valid @RequestBody ExamenDTO dto) throws Exception {
		Examen obj = service.registrar(mapper.map(dto, Examen.class));

		// localhost:8080/pacientes/5
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(obj.getIdExamen()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping
	public ResponseEntity<ExamenDTO> modificar(@Valid @RequestBody ExamenDTO dto) throws Exception {

		Examen obj = service.listarPorId(dto.getIdExamen());

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO: " + dto.getIdExamen());
		}

		Examen pac = service.modificar(mapper.map(dto, Examen.class));
		ExamenDTO dtoResponse = mapper.map(pac, ExamenDTO.class);

		return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {

		Examen obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO: " + id);
		}

		service.eliminar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/hateoas/{id}")
	public EntityModel<ExamenDTO> listarHateoas(@PathVariable("id") Integer id) throws Exception {
		Examen obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		ExamenDTO dto = mapper.map(obj, ExamenDTO.class);
		
		EntityModel<ExamenDTO> recurso = EntityModel.of(dto);
		//localhost:8080/pacientes/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarPorId(id));
		recurso.add(link1.withRel("examen-info"));	
		return recurso;
	}
}