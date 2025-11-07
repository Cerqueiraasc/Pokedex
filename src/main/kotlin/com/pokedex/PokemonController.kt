package com.pokedex

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pokemon")
class PokemonController(
    private val pokeApiService: PokeApiService
) {
    @GetMapping("/{name}")
    suspend fun getPokemon(@PathVariable name: String): PokemonSimplificado
    {
        return pokeApiService.getPokemon(name)
    }
    @ExceptionHandler(PokemonNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun hadleNotFound(e: PokemonNotFoundException): Map<String, String>
    {
        return mapOf("error" to (e.message ?: "Não encontrado"))
    }
    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun handleServiceError(e: IllegalArgumentException): Map<String, String>
    {
        return mapOf("error" to (e.message ?: "Erro de serviço"))
    }
}
