package com.pokedex

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PokemonRepository: JpaRepository<PokemonSalvo, Int>