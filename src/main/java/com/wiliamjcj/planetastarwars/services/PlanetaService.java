package com.wiliamjcj.planetastarwars.services;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.wiliamjcj.planetastarwars.dto.DTOMapper;
import com.wiliamjcj.planetastarwars.dto.PlanetaDTO;
import com.wiliamjcj.planetastarwars.entities.Planeta;
import com.wiliamjcj.planetastarwars.repositories.PlanetaRepository;

@Service
public class PlanetaService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DTOMapper mapper;

	@Autowired
	PlanetaRepository planetaRepository;

	@Value("${rest.default.user-agent}")
	private String userAgent;

	@Value("${rest.swapi.uri}")
	private String swapiURI;

	public PlanetaDTO criarPlaneta(PlanetaDTO dto) {
		Planeta planeta = (Planeta) mapper.mapPlanetaDTOToPlaneta(dto);

		Integer qtdAparicoesFilmes = buscarPlanetaSWAPI(planeta.getNome());
		planeta.setQtdAparicoesFilmes(qtdAparicoesFilmes);

		planeta = planetaRepository.save(planeta);

		return (PlanetaDTO) mapper.mapPlanetaToPlanetaDTO(planeta);
	}

	public Integer buscarPlanetaSWAPI(String nome) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
			headers.add("user-agent", userAgent);
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<String> response = restTemplate.exchange(swapiURI + nome, HttpMethod.GET, entity,
					String.class);

			JSONObject jo = new JSONObject(response.getBody());
			Integer qtd = jo.getInt("count");
			if (qtd > 1) {
				return 0;
			}
			JSONObject planeta = (JSONObject) jo.getJSONArray("results").get(0);
			JSONArray filmes = planeta.getJSONArray("films");
			return filmes.length();
		} catch (Exception e) {
			log.error(e.getMessage());
			log.debug(e.getStackTrace().toString());
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public List<PlanetaDTO> buscarPlanetas() {
		List<Planeta> planetas = planetaRepository.findAll();
		Type tipo = new TypeToken<List<PlanetaDTO>>() {
		}.getType();
		List<PlanetaDTO> planetasDTO = (List<PlanetaDTO>) mapper.map(planetas, tipo);
		return planetasDTO;
	}

	@SuppressWarnings("unchecked")
	public List<PlanetaDTO> buscarPlanetas(String nome) {
		List<Planeta> planetas = planetaRepository.findByNomeContainingIgnoreCase(nome);
		Type tipo = new TypeToken<List<PlanetaDTO>>() {
		}.getType();
		List<PlanetaDTO> planetasDTO = (List<PlanetaDTO>) mapper.map(planetas, tipo);
		return planetasDTO;
	}

	public PlanetaDTO buscarPlanetaPorId(String id) {
		Optional<Planeta> planeta = planetaRepository.findById(id);
		if (planeta.isPresent())
			return (PlanetaDTO) mapper.map(planeta.get(), PlanetaDTO.class);
		else
			return new PlanetaDTO();
	}

	public void deletarPlaneta(PlanetaDTO dto) {
		Planeta planeta = (Planeta) mapper.map(dto, Planeta.class);
		planetaRepository.delete(planeta);
	}

}
