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

import com.mitocode.dto.EspecialidadDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Especialidad;
import com.mitocode.service.IEspecialidadService;

@RestController
@RequestMapping("/especialidades")
public class EspecialidadController {

	@Autowired
	private IEspecialidadService service;

	@Autowired
	private ModelMapper mapper;

	@GetMapping
	// @RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<EspecialidadDTO>> listar() throws Exception {
		List<EspecialidadDTO> lista = service.listar().stream().map(p -> mapper.map(p, EspecialidadDTO.class))
				.collect(Collectors.toList());

		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EspecialidadDTO> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Especialidad obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		EspecialidadDTO dto = mapper.map(obj, EspecialidadDTO.class);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/*
	 * @PostMapping public ResponseEntity<Especialidad> registrar(@RequestBody Especialidad
	 * p) throws Exception { Especialidad obj = service.registrar(p); return new
	 * ResponseEntity<Especialidad>(obj, HttpStatus.CREATED); }
	 */

	@PostMapping // Debe devolver la Url para que el que lo consuma tenga más información
	public ResponseEntity<Void> registrar(@Valid @RequestBody EspecialidadDTO dto) throws Exception {
		Especialidad obj = service.registrar(mapper.map(dto, Especialidad.class));

		// localhost:8080/pacientes/5
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(obj.getIdEspecialidad()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping
	public ResponseEntity<EspecialidadDTO> modificar(@Valid @RequestBody EspecialidadDTO dto) throws Exception {

		Especialidad obj = service.listarPorId(dto.getIdEspecialidad());

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO: " + dto.getIdEspecialidad());
		}

		Especialidad pac = service.modificar(mapper.map(dto, Especialidad.class));
		EspecialidadDTO dtoResponse = mapper.map(pac, EspecialidadDTO.class);

		return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {

		Especialidad obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO: " + id);
		}

		service.eliminar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/hateoas/{id}")
	public EntityModel<EspecialidadDTO> listarHateoas(@PathVariable("id") Integer id) throws Exception {
		Especialidad obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		EspecialidadDTO dto = mapper.map(obj, EspecialidadDTO.class);
		
		EntityModel<EspecialidadDTO> recurso = EntityModel.of(dto);
		//localhost:8080/pacientes/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarPorId(id));
		recurso.add(link1.withRel("especialidad-info"));	
		return recurso;
	}
}