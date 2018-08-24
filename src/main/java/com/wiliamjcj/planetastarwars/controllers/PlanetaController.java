package com.wiliamjcj.planetastarwars.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiliamjcj.planetastarwars.services.PlanetaService;

@RestController
@RequestMapping(value = "${controller.planeta.mapping}")
public class PlanetaController {

	@Value("${controller.planeta.mapping}")
	private String BASE_ENDPOINT;

	@Autowired
	PlanetaService planetaService;
	
}
