package com.wiliamjcj.planetastarwars.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiliamjcj.planetastarwars.dto.PlanetaDTO;
import com.wiliamjcj.planetastarwars.services.PlanetaService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PlanetaController.class, secure = false)
public class PlanetaControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PlanetaService planetaService;

	@Value("${controller.planeta.mapping}")
	private String BASE_ENDPOINT;

	private static ObjectMapper mapper = new ObjectMapper();

	private static PlanetaDTO planeta1;
	private static PlanetaDTO planeta2;
	private static PlanetaDTO planetaAdicionado;
	private static List<PlanetaDTO> todosPlanetas;
	private static List<PlanetaDTO> planetasPorNome;

	@BeforeClass
	public static void setup() {
		planeta1 = new PlanetaDTO();
		planeta1.setNome("Alderan");
		planeta1.setClima("Chuvoso");
		planeta1.setTerreno("Selva");
		planeta1.setId("5b801e59c28cb5300cc2e001");
		planeta1.setQtdAparicoesFilmes(0);

		planeta2 = new PlanetaDTO();
		planeta2.setNome("Alderan");
		planeta2.setClima("Chuvoso");

		planetaAdicionado = new PlanetaDTO();
		planetaAdicionado.setNome("Tatooine");
		planetaAdicionado.setClima("Seco");
		planetaAdicionado.setTerreno("Deserto");
		planetaAdicionado.setId("5b801e59c28cb5300cc2e002");
		planetaAdicionado.setQtdAparicoesFilmes(5);

		todosPlanetas = new ArrayList<PlanetaDTO>();
		todosPlanetas.add(planeta1);
		todosPlanetas.add(planetaAdicionado);

		planetasPorNome = new ArrayList<PlanetaDTO>();
		planetasPorNome.add(planeta1);
	}

	@Test
	public void adicionarPlanetaTest() throws Exception {

		Mockito.when(planetaService.criarPlaneta(Mockito.any())).thenReturn(planetaAdicionado);

		String planetaJson = mapper.writeValueAsString(planeta1);
		String planetaJsonEsperado = mapper.writeValueAsString(planetaAdicionado);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8).content(planetaJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status);

		String location = result.getResponse().getHeader("location");
		assertThat(location).isNotBlank();

		JSONObject jo = new JSONObject(result.getResponse().getContentAsString());
		Object planetaJsonRetorno = jo.get("data");

		JSONAssert.assertEquals(planetaJsonEsperado, planetaJsonRetorno.toString(), false);
	}

	@Test
	public void adicionarPlanetaValidacaoTest() throws Exception {

		Mockito.when(planetaService.criarPlaneta(Mockito.any())).thenReturn(planetaAdicionado);
		String planetaJson = mapper.writeValueAsString(planeta2);
		String resultadoEsperado = "{\"data\":null,\"errors\":[\"Favor informar o tipo de terreno!\"]}";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8).content(planetaJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.BAD_REQUEST.value(), status);

		JSONObject jo = new JSONObject(result.getResponse().getContentAsString());

		JSONAssert.assertEquals(resultadoEsperado, jo, false);
	}

	@Test
	public void adicionarPlanetaExceptionTest() throws Exception {

		Mockito.when(planetaService.criarPlaneta(Mockito.any())).thenThrow(new NullPointerException());
		String planetaJson = mapper.writeValueAsString(planeta1);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8).content(planetaJson)
				.contentType(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
	}

	@Test
	public void listarPlanetasTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetas()).thenReturn(todosPlanetas);
		String resultadoEsperado = mapper.writeValueAsString(todosPlanetas);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);

		JSONObject jo = new JSONObject(result.getResponse().getContentAsString());
		Object planetaJsonRetorno = jo.get("data");

		JSONAssert.assertEquals(resultadoEsperado, planetaJsonRetorno.toString(), false);

	}

	@Test
	public void listarPlanetasExceptionTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetas()).thenThrow(new NullPointerException());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
	}

	@Test
	public void buscarPorNomeTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetas(Mockito.anyString())).thenReturn(planetasPorNome);
		String resultadoEsperado = mapper.writeValueAsString(planetasPorNome);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + "?nome=" + planeta1.getNome())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);

		JSONObject jo = new JSONObject(result.getResponse().getContentAsString());
		Object planetaJsonRetorno = jo.get("data");

		JSONAssert.assertEquals(resultadoEsperado, planetaJsonRetorno.toString(), false);
	}

	@Test
	public void buscarPorNomeSemResultadoTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetas(Mockito.anyString())).thenReturn(new ArrayList<PlanetaDTO>());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + "?nome=" + planeta1.getNome())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.NO_CONTENT.value(), status);
	}

	@Test
	public void buscarPorNomeExceptionTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetas(Mockito.anyString())).thenThrow(new NullPointerException());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + "?nome=" + planeta1.getNome())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
	}

	@Test
	public void buscarPorIdTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetaPorId(Mockito.anyString())).thenReturn(planeta1);
		String resultadoEsperado = mapper.writeValueAsString(planeta1);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + "/" + planeta1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);

		JSONObject jo = new JSONObject(result.getResponse().getContentAsString());
		Object planetaJsonRetorno = jo.get("data");

		JSONAssert.assertEquals(resultadoEsperado, planetaJsonRetorno.toString(), false);
	}

	@Test
	public void buscarPorIdSemResultadoTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetaPorId(Mockito.anyString())).thenReturn(new PlanetaDTO());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + "/" + planeta1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.NO_CONTENT.value(), status);
	}

	@Test
	public void buscarPorIdExceptionTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetaPorId(Mockito.anyString())).thenThrow(new NullPointerException());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + "/" + planeta1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
	}

	@Test
	public void removerPlanetaTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetaPorId(Mockito.anyString())).thenReturn(planeta1);
		Mockito.doNothing().when(planetaService).deletarPlaneta(Mockito.any());

		String resultadoEsperado = mapper.writeValueAsString(planeta1);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BASE_ENDPOINT + "/" + planeta1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.OK.value(), status);

		JSONObject jo = new JSONObject(result.getResponse().getContentAsString());
		Object planetaJsonRetorno = jo.get("data");

		JSONAssert.assertEquals(resultadoEsperado, planetaJsonRetorno.toString(), false);
	}

	@Test
	public void removerPlanetaSemResultadoTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetaPorId(Mockito.anyString())).thenReturn(new PlanetaDTO());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BASE_ENDPOINT + "/" + planeta1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.NO_CONTENT.value(), status);
	}

	@Test
	public void removerPlanetaExceptionTest() throws Exception {
		Mockito.when(planetaService.buscarPlanetaPorId(Mockito.anyString())).thenThrow(new NullPointerException());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BASE_ENDPOINT + "/" + planeta1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
	}
}
