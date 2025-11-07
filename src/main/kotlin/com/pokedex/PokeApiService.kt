package com.pokedex

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

@Service
class PokeApiService(
    private val pokeApiWebClient: WebClient
) {

    suspend fun getPokemon(name: String): PokemonSimplificado {

        try {
            val pokemonData = pokeApiWebClient.get()
                .uri("/pokemon/{name}", name.lowercase())
                .retrieve()
                .awaitBody<PokemonApiResponse>()

//            return PokemonApiResponse(
//                id = pokemonData.id,
//                name = pokemonData.name,
//                types = pokemonData.types[0].type,
//                sprites = pokemonData.sprites
//            )

            return PokemonSimplificado(
                nome = pokemonData.name,
                id = pokemonData.id,
                tipo_principal = pokemonData.types.get(0).type.name,
                imagem = pokemonData.sprites.frontDefault
            )

        } catch (e: WebClientResponseException.NotFound) {
            throw PokemonNotFoundException("Pokémon '$name' não encontrado.")
        } catch (e: Exception) {
            throw IllegalStateException("Erro ao consultar a PokéAPI: ${e.message}")
        }
    }
}

class PokemonNotFoundException(message: String) : RuntimeException(message)