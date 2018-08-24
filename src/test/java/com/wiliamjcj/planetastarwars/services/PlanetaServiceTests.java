package com.wiliamjcj.planetastarwars.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wiliamjcj.planetastarwars.dto.DTOMapper;
import com.wiliamjcj.planetastarwars.dto.PlanetaDTO;
import com.wiliamjcj.planetastarwars.entities.Planeta;
import com.wiliamjcj.planetastarwars.repositories.PlanetaRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PlanetaService.class, secure = false)
public class PlanetaServiceTests {
	
	
	@TestConfiguration
    static class PlanetaServiceTestsContextConfiguration {
        @Bean
        public PlanetaService planetaService() {
            PlanetaService service = new PlanetaService();
			return service;
        }
    }
	
	@Value("${rest.default.user-agent}")
	private String userAgent;
	
	@Value("${rest.swapi.uri}")
	private String swapiURI;
	
	@Autowired
	PlanetaService planetaService;
	
	@MockBean
	PlanetaRepository planetaRepository;
	
	@MockBean
	DTOMapper dtoMapper;
	
	@Autowired
	DTOMapper dtoMapperTest;
	
	private static PlanetaDTO planetaDto1;
	private static PlanetaDTO planetaDto2;
	private static Planeta planeta1;
	private static Planeta planeta2;
	private static Planeta planetaAdicionado;
	private static PlanetaDTO planetaEsperado;
	
	private static List<Planeta> todosPlanetas;
	private static List<PlanetaDTO> todosDTOs;
	private static List<Planeta> planetaPorNome;
	private static List<PlanetaDTO> dtoPorNome;
	
	@BeforeClass
	public static void setup() {
		planetaDto1 = new PlanetaDTO();
		planetaDto1.setNome("Tatooine");
		planetaDto1.setClima("Seco");
		planetaDto1.setTerreno("Deserto");
	
		planetaDto2 = new PlanetaDTO();
		planetaDto2.setNome("Alderan");
		planetaDto2.setClima("Chuvoso");
		planetaDto2.setTerreno("Selva");
		
		planeta1 = new Planeta();
		planeta1.setNome("Tatooine");
		planeta1.setClima("Seco");
		planeta1.setTerreno("Deserto");
		
		planeta2 = new Planeta();
		planeta2.setNome("Alderan");
		planeta2.setClima("Chuvoso");
		planeta2.setTerreno("Selva");
		
		planetaAdicionado = new Planeta();
		planetaAdicionado.setNome("Tatooine");
		planetaAdicionado.setClima("Seco");
		planetaAdicionado.setTerreno("Deserto");
		planetaAdicionado.setId("5b801e59c28cb5300cc2e002");
		planetaAdicionado.setQtdAparicoesFilmes(5);
		
		planetaEsperado = new PlanetaDTO();
		planetaEsperado.setNome("Tatooine");
		planetaEsperado.setClima("Seco");
		planetaEsperado.setTerreno("Deserto");
		planetaEsperado.setId("5b801e59c28cb5300cc2e002");
		planetaEsperado.setQtdAparicoesFilmes(5);
		
		todosPlanetas = new ArrayList<Planeta>();
		todosPlanetas.add(planeta1);
		todosPlanetas.add(planeta2);
		
		todosDTOs = new ArrayList<PlanetaDTO>();
		todosDTOs.add(planetaDto1);
		todosDTOs.add(planetaDto2);
		
		planetaPorNome = new ArrayList<Planeta>();
		planetaPorNome.add(planeta2);
		
		dtoPorNome = new ArrayList<PlanetaDTO>();
		dtoPorNome.add(planetaDto2);
	}
	
	@Test
	public void criarPlanetaTest() throws JsonProcessingException {
		Mockito.when(planetaRepository.save(Mockito.any())).thenReturn(planetaAdicionado);
		
		Mockito.when(dtoMapper.mapPlanetaDTOToPlaneta(Mockito.any())).thenReturn(planeta1);
		Mockito.when(dtoMapper.mapPlanetaToPlanetaDTO(Mockito.any())).thenReturn(planetaEsperado);
		
		PlanetaDTO retorno = planetaService.criarPlaneta(planetaDto1);
		
		assertEquals(retorno, planetaEsperado);
	}
	
	@Test
	public void acessoSwapiOkTest() throws JsonProcessingException {
		Integer qtd = planetaService.buscarPlanetaSWAPI(planeta1.getNome());
		Integer qtdEsperado = 5;
		
		assertEquals(qtd,qtdEsperado);
	}
	
	@Test
	public void acessoSwapiMaisDeUmTest() throws JsonProcessingException {
		Integer qtd = planetaService.buscarPlanetaSWAPI("ta");
		Integer qtdEsperado = 0;
		
		assertEquals(qtd,qtdEsperado);
	}
	
	@Test
	public void acessoSwapiInexistenteTest() throws JsonProcessingException {
		Integer qtd = planetaService.buscarPlanetaSWAPI("Marte");
		Integer qtdEsperado = 0;
		
		assertEquals(qtd,qtdEsperado);
	}
	
	@Test
	public void buscarPlanetasTest() throws JsonProcessingException {
		Mockito.when(planetaRepository.findAll()).thenReturn(todosPlanetas);
		Mockito.when(dtoMapper.map(Mockito.any(),Mockito.any())).thenReturn(todosDTOs);
		
		List<PlanetaDTO> retorno = planetaService.buscarPlanetas();
		assertTrue(todosDTOs.stream().allMatch(
				planeta -> retorno.contains(planeta))
		);
	}
	
	@Test
	public void buscarPlanetasPorNomeTest() throws JsonProcessingException {
		Mockito.when(planetaRepository.findByNomeContainingIgnoreCase(Mockito.anyString())).thenReturn(planetaPorNome);
		Mockito.when(dtoMapper.map(Mockito.any(),Mockito.any())).thenReturn(dtoPorNome);
		
		List<PlanetaDTO> retorno = planetaService.buscarPlanetas(planeta2.getNome());
		assertTrue(dtoPorNome.stream().allMatch(
				planeta -> retorno.contains(planeta))
		);
	}
	
	
	@Test
	public void buscarPlanetaPorIdInexistenteTest() throws JsonProcessingException {
		Mockito.when(planetaRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
		
		PlanetaDTO retorno = planetaService.buscarPlanetaPorId(planeta1.getId());
		assertFalse(planetaDto2.equals(retorno));
	}

}
