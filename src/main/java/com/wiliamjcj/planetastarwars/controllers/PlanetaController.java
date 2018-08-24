package com.wiliamjcj.planetastarwars.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiliamjcj.planetastarwars.dto.PlanetaDTO;
import com.wiliamjcj.planetastarwars.services.PlanetaService;
import com.wiliamjcj.planetastarwars.utils.APIResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "${controller.planeta.mapping}")
public class PlanetaController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${controller.planeta.mapping}")
	private String BASE_ENDPOINT;

	@Autowired
	PlanetaService planetaService;

	@ApiOperation(value = "Adiciona um novo planeta, com nome, clima e terreno, retornando no header a sua localização.")
	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<APIResponse<String>> adicionarPlaneta(@Valid @RequestBody PlanetaDTO planeta,
			BindingResult res) {
		try {
			APIResponse<String> apiResponse = new APIResponse<String>();

			if (res.hasErrors()) {
				res.getAllErrors().stream().forEach(err -> apiResponse.getErrors().add(err.getDefaultMessage()));
				return ResponseEntity.badRequest().body(apiResponse);
			}
			planeta.setId(null);
			planeta = planetaService.criarPlaneta(planeta);
			URI location = new URI(BASE_ENDPOINT + "/" + planeta.getId());

			return ResponseEntity.created(location).body(apiResponse);
		} catch (Exception e) {
			log.error(e.getMessage());
			log.debug(e.getStackTrace().toString());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
