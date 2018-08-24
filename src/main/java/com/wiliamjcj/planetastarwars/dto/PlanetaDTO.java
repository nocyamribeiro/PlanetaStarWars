package com.wiliamjcj.planetastarwars.dto;

import javax.validation.constraints.NotEmpty;

public class PlanetaDTO {

	private String id;
	
	@NotEmpty(message="Favor informar o nome do planeta!")
	private String nome;
	
	@NotEmpty(message="Favor informar o tipo de clima!")
	private String clima;
	
	@NotEmpty(message="Favor informar o tipo de terreno!")
	private String terreno;
	
	private int qtdAparicoesFilmes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getClima() {
		return clima;
	}

	public void setClima(String clima) {
		this.clima = clima;
	}

	public String getTerreno() {
		return terreno;
	}

	public void setTerreno(String terreno) {
		this.terreno = terreno;
	}

	public int getQtdAparicoesFilmes() {
		return qtdAparicoesFilmes;
	}

	public void setQtdAparicoesFilmes(int qtdAparicoesFilmes) {
		this.qtdAparicoesFilmes = qtdAparicoesFilmes;
	}
}
