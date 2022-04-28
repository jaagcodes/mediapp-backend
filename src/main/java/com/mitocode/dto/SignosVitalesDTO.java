package com.mitocode.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

public class SignosVitalesDTO {
	
	private Integer id;
	
	@NotNull
	private PacienteDTO paciente;
	
	@NotNull
	private LocalDateTime fecha;
	
	@NotNull
	private String pulso;
	
	@NotNull
	private String temperatura;
	
	@NotNull
	private String ritmo;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PacienteDTO getPaciente() {
		return paciente;
	}

	public void setPaciente(PacienteDTO paciente) {
		this.paciente = paciente;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public String getPulso() {
		return pulso;
	}

	public void setPulso(String pulso) {
		this.pulso = pulso;
	}

	public String getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(String temperatura) {
		this.temperatura = temperatura;
	}

	public String getRitmo() {
		return ritmo;
	}

	public void setRitmo(String ritmo) {
		this.ritmo = ritmo;
	}
	

}
