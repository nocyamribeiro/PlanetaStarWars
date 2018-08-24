package com.wiliamjcj.planetastarwars.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.wiliamjcj.planetastarwars.entities.Planeta;

public interface PlanetaRepository extends MongoRepository<Planeta, String> {

	Optional<Planeta> findById(String id);

	List<Planeta> findByNomeContainingIgnoreCase(String nome);
}
