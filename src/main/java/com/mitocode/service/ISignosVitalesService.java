package com.mitocode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mitocode.model.SignosVitales;

public interface ISignosVitalesService extends ICRUD<SignosVitales, Integer>{

	Page<SignosVitales> listarPageable(Pageable page);
}
