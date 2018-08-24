package com.wiliamjcj.planetastarwars.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wiliamjcj.planetastarwars.dto.DTOMapper;
import com.wiliamjcj.planetastarwars.repositories.PlanetaRepository;

@Service
public class PlanetaService {

	@Autowired
	DTOMapper mapper;

	@Autowired
	PlanetaRepository planetaRepository;
	
}
