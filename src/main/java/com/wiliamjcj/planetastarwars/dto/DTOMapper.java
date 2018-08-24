package com.wiliamjcj.planetastarwars.dto;

import java.lang.reflect.Type;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.wiliamjcj.planetastarwars.entities.Planeta;

@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class DTOMapper {

	private ModelMapper modelMapper;
	
	public DTOMapper() {
		modelMapper = new ModelMapper();
	}
	
	public Object mapPlanetaDTOToPlaneta(PlanetaDTO dto) {
		return map(dto, Planeta.class);
	}
	
	public Object mapPlanetaToPlanetaDTO(Planeta planeta) {
		return map(planeta, PlanetaDTO.class);
	}
	
	public Object map(Object obj, Type tipo) {
		return modelMapper.map(obj, tipo);
	}
	
	public <S,D> Page<D> mapPage(Page<S> page, Class<D> tipoClasse) {
		Page<Object> pageObj = page.map(e -> modelMapper.map(e, tipoClasse));
		Type tipo = new TypeToken<Page<D>>() {}.getType();
		return modelMapper.map(pageObj, tipo);
	}

	
	
}

