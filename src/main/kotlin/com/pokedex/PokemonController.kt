package com.pokedex

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pokemon")
class PokemonController(
    private val pokeApiService: PokeApiService
) {
    @GetMapping("/{name}")
    suspend fun getPokemon(@PathVariable name: String): PokemonSimplificado {
        return pokeApiService.getPokemon(name)
    }
}