package com.mitocode.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mitocode.model.Consulta;

//@Repository NO es necesario (redundante)
public interface IConsultaRepo extends IGenericRepo<Consulta, Integer> {
    //JPQL Java Persistence Query Language
	@Query("FROM Consulta c WHERE c.paciente.dni = :dni OR LOWER(c.paciente.nombres) LIKE %:nombreCompleto% OR LOWER(c.paciente.apellidos) LIKE %:nombreCompleto%")
	List<Consulta> buscar(@Param("dni") String dni, @Param("nombreCompleto") String nombreCompleto);
	
	//>=12-01-2022 <19-02-2022
	@Query("FROM Consulta c WHERE c.fecha BETWEEN :fechaConsulta1 AND :fechaConsulta2")
	List<Consulta> buscarFecha(@Param("fechaConsulta1") LocalDateTime fechaConsulta, @Param("fechaConsulta2") LocalDateTime fechaConsulta2);
	
	@Query(value = "select * from fn_listarResumen()", nativeQuery = true)
	List<Object[]> listarResumen();
}
