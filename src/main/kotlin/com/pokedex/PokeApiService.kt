package com.pokedex

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

@Service
class PokeApiService(
    private val pokeApiWebClient: WebClient
) {

    companion object {
        private val logger = LoggerFactory.getLogger(PokeApiService::class.java)
    }

    suspend fun getPokemon(name: String): PokemonSimplificado {
        val lowercaseName = name.lowercase()
        try {
            val pokemonData = pokeApiWebClient.get()
                .uri("/pokemon/{name}", lowercaseName)
                .retrieve()
                .awaitBody<PokemonApiResponse>()

            return pokemonData.toSimplificado()

        } catch (e: WebClientResponseException.NotFound) {
            logger.warn("Pokémon não encontrado com o nome: '$lowercaseName'", e)
            throw PokemonNotFoundException("Pokémon '$name' não encontrado.")

        } catch (e: WebClientResponseException) {
            logger.error(
                "Erro ${e.statusCode} ao consultar PokéAPI para '$lowercaseName'. " +
                        "Isso geralmente indica falha na deserialização (JSON -> Data Class). " +
                        "Resposta: ${e.responseBodyAsString}", e
            )
            throw IllegalStateException("Erro ${e.statusCode} ao processar dados da PokéAPI.")

        } catch (e: WebClientRequestException) {
            logger.error("Erro de conexão ao consultar PokéAPI para '$lowercaseName'", e)
            throw IllegalStateException("Erro de conexão ao consultar a PokéAPI: ${e.message}")
        }
    }
}

private fun PokemonApiResponse.toSimplificado(): PokemonSimplificado {
    return PokemonSimplificado(
        nome = this.name ?: "sem-nome",
        id = this.id ?: -1,
        tipoPrincipal = this.types?.firstOrNull()?.type?.name ?: "desconhecido",
        imagem = this.sprites?.frontDefault
    )
}

class PokemonNotFoundException(message: String) : RuntimeException(message)