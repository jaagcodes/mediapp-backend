package com.mitocode.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.dto.ConsultaDTO;
import com.mitocode.dto.ConsultaListaExamenDTO;
import com.mitocode.dto.ConsultaResumenDTO;
import com.mitocode.dto.FiltroConsultaDTO;
import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Archivo;
import com.mitocode.model.Consulta;
import com.mitocode.model.Examen;
import com.mitocode.service.IArchivoService;
import com.mitocode.service.IConsultaService;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

	@Autowired
	private IConsultaService service;
	
	@Autowired
	private IArchivoService serviceArchivo;

	@Autowired
	private ModelMapper mapper;

	@GetMapping
	// @RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<List<ConsultaDTO>> listar() throws Exception {
		List<ConsultaDTO> lista = service.listar().stream().map(p -> mapper.map(p, ConsultaDTO.class))
				.collect(Collectors.toList());

		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ConsultaDTO> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Consulta obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		ConsultaDTO dto = mapper.map(obj, ConsultaDTO.class);

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	/*
	 * @PostMapping public ResponseEntity<Consulta> registrar(@RequestBody Consulta
	 * p) throws Exception { Consulta obj = service.registrar(p); return new
	 * ResponseEntity<Consulta>(obj, HttpStatus.CREATED); }
	 */

	// Debe devolver la Url para que el que lo consuma tenga más información
	@PostMapping 
	public ResponseEntity<Void> registrar(@Valid @RequestBody ConsultaListaExamenDTO dto) throws Exception {
		Consulta c = mapper.map(dto.getConsulta(), Consulta.class);
		List<Examen> examenes  = mapper.map(dto.getLstExamen(), new TypeToken<List<Examen>>() {}.getType());
		
		Consulta obj = service.registrarTransaccional(c, examenes);
		//Consulta obj = service.registrarTransaccional(mapper.map(dto.getConsulta(), Consulta.class), mapper.map(dto.getLstExamen(), new TypeToken<List<Examen>>() {}.getType()));

		// localhost:8080/pacientes/5
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(obj.getIdConsulta()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping
	public ResponseEntity<ConsultaDTO> modificar(@Valid @RequestBody ConsultaDTO dto) throws Exception {

		Consulta obj = service.listarPorId(dto.getIdConsulta());

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO: " + dto.getIdConsulta());
		}

		Consulta pac = service.modificar(mapper.map(dto, Consulta.class));
		ConsultaDTO dtoResponse = mapper.map(pac, ConsultaDTO.class);

		return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {

		Consulta obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO: " + id);
		}

		service.eliminar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/hateoas/{id}")
	public EntityModel<ConsultaDTO> listarHateoas(@PathVariable("id") Integer id) throws Exception {
		Consulta obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		ConsultaDTO dto = mapper.map(obj, ConsultaDTO.class);
		
		EntityModel<ConsultaDTO> recurso = EntityModel.of(dto);
		//localhost:8080/pacientes/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarPorId(id));
		recurso.add(link1.withRel("especialidad-info"));	
		return recurso;
	}
	
	@GetMapping("/buscar")
	public ResponseEntity<List<ConsultaDTO>> buscarFecha(@RequestParam(value = "fecha1") String fecha1, @RequestParam(value = "fecha2") String fecha2) {
		List<Consulta> consultas = new ArrayList<>();
		
		consultas = service.buscarFecha(LocalDateTime.parse(fecha1), LocalDateTime.parse(fecha2));
		List<ConsultaDTO> consultasDTO = mapper.map(consultas, new TypeToken<List<ConsultaDTO>>() {}.getType()); 
		
		return new ResponseEntity<>(consultasDTO, HttpStatus.OK);
	}
	
	@PostMapping("/buscar/otros")
	public ResponseEntity<List<ConsultaDTO>> buscarOtro(@RequestBody FiltroConsultaDTO filtro){
		List<Consulta> consultas = new ArrayList<>();
		
		consultas = service.buscar(filtro.getDni(), filtro.getNombreCompleto());
		List<ConsultaDTO> consultasDTO = mapper.map(consultas, new TypeToken<List<ConsultaDTO>>() {}.getType());
		
		return new ResponseEntity<List<ConsultaDTO>>(consultasDTO, HttpStatus.OK);
		
	}
	
	@GetMapping(value = "/listarResumen")
	public ResponseEntity<List<ConsultaResumenDTO>> listarResumen(){
		List<ConsultaResumenDTO> consultas = new ArrayList<>();
		consultas = service.listarResumen();
		return new ResponseEntity<>(consultas, HttpStatus.OK);
	}
	
	@GetMapping(value = "/generarReporte", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)// APPLICATION_PDF_VALUE  APPLICATION_OCTET_STREAM_VALUE
	public ResponseEntity<byte[]> generarReporte(){
		byte[] data = null;
		data = service.generarReporte();
		return new ResponseEntity<>(data, HttpStatus.OK);
	}
	
	@PostMapping(value = "/guardarArchivo", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Integer> guardarArchivo(@RequestParam("adjunto") MultipartFile file) throws Exception{
		int rpta = 0;
		
		Archivo ar = new Archivo();
		ar.setFiletype(file.getContentType());
		ar.setFilename(file.getOriginalFilename());
		ar.setValue(file.getBytes());
		
		rpta = serviceArchivo.guardar(ar);
		
		return new ResponseEntity<>(rpta, HttpStatus.OK);
	}
	
	@GetMapping(value = "/leerArchivo/{idArchivo}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> leerArchivo(@PathVariable("idArchivo") Integer idArchivo)throws IOException{
		
		byte[] arr = serviceArchivo.leerArchivo(idArchivo);
		
		return new ResponseEntity<>(arr, HttpStatus.OK);
		
	}
	
	
	
}