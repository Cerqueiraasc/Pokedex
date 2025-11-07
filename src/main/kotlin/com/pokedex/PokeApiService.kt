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

    val statsMapeadas = this.stats?.mapNotNull { statSlot ->
        val nomeStat = statSlot.stat?.name ?: return@mapNotNull null
        val valorStat = statSlot.baseStat ?: 0

        val nomeTraduzido = when (nomeStat) {
            "hp" -> "PS"
            "attack" -> "ATK"
            "defense" -> "DEF"
            "special-attack" -> "SP.ATK"
            "special-defense" -> "SP.DEF"
            "speed" -> "SPEED"
            else -> null
        }

        if (nomeTraduzido != null) {
            StatSimplificado(nome = nomeTraduzido, valor = valorStat)
        } else {
            null
        }
    }?.sortedBy {
        when (it.nome) {
            "PS" -> 1
            "ATK" -> 2
            "DEF" -> 3
            "SP.ATK" -> 4
            "SP.DEF" -> 5
            "SPEED" -> 6
            else -> 7
        }
    }

    return PokemonSimplificado(
        nome = this.name ?: "sem-nome",
        id = this.id ?: -1,
        tipoPrincipal = this.types?.firstOrNull()?.type?.name ?: "desconhecido",
        imagem = this.sprites?.frontDefault,
        altura = this.height,
        peso = this.weight,
        stats = statsMapeadas
    )
}

class PokemonNotFoundException(message: String) : RuntimeException(message)