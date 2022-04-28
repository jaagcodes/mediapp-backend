package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mitocode.model.SignosVitales;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.ISignosVitalesRepo;
import com.mitocode.service.ISignosVitalesService;

@Service
public class SignosVitalesServiceImpl extends CRUDImpl<SignosVitales, Integer> implements ISignosVitalesService{

	@Autowired
	private ISignosVitalesRepo repo;
	
	@Override
	protected IGenericRepo<SignosVitales, Integer> getRepo() {
		return repo;
	}

	@Override
	public Page<SignosVitales> listarPageable(Pageable page) {
		return repo.findAll(page);
	}

}
