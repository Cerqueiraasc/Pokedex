package com.pokedex

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

@Service
class PokeApiService(
    private val pokeApiWebClient: WebClient,
    private val repository: PokemonRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PokeApiService::class.java)
    }

    fun getAll(): List<PokemonSalvo>{
        return repository.findAll()
    }

    suspend fun getPokemon(name: String): PokemonSimplificado {
        val lowercaseName = name.lowercase()
        try {
            val pokemon = pokeApiWebClient.get()
                .uri("/pokemon/{name}", lowercaseName)
                .retrieve()
                .awaitBody<PokemonApiResponse>()

            val evolucoes = getEvolucoes(pokemon.species?.url)

            return pokemon.toSimplificado(evolucoes)

        } catch (e: WebClientResponseException.NotFound) {
            logger.warn("Pokemon não encontrado com o nome: '$lowercaseName'", e)
            throw PokemonNotFoundException("Pokemon '$name' não encontrado.")

        } catch (e: WebClientResponseException) {
            logger.error("Erro de conexão ao consultar PokéAPI para '$lowercaseName'", e)
            throw IllegalStateException("Erro de conexão ao consultar a PokéAPI: ${e.message}")
        }
    }

    private suspend fun getEvolucoes(speciesUrl: String?): List<String>? {
        if (speciesUrl == null) return null

        return try {
            val speciesData = pokeApiWebClient.get()
                .uri(URI.create(speciesUrl))
                .retrieve()
                .awaitBody<SpeciesApiResponse>()

            val evolutionChainUrl = speciesData.evolutionChain?.url ?: return null

            val evolutionChainData = pokeApiWebClient.get()
                .uri(URI.create(evolutionChainUrl))
                .retrieve()
                .awaitBody<EvolutionChainApiResponse>()

            parseEvolutionChain(evolutionChainData.chain)

        } catch (e: Exception) {
            logger.error("Falha ao buscar cadeia de evolução para $speciesUrl", e)
            null
        }
    }

    private fun parseEvolutionChain(chainLink: ChainLink?): List<String> {
       if (chainLink == null) return emptyList()

        val evolucoes = mutableListOf<String>()

        chainLink.species?.name?.let { evolucoes.add(it) }

        chainLink.evolvesTo?.forEach { proximaEvolucao ->
            evolucoes.addAll(parseEvolutionChain(proximaEvolucao))
        }
        return evolucoes
    }

    suspend fun salvarPokemon(pokemon: PokemonSimplificado) {
        try {
            val pokemonParaSalvar = PokemonSalvo(
                id = pokemon.id,
                nome = pokemon.nome,
                tipoPrincipal = pokemon.tipoPrincipal,
                imagem = pokemon.imagem
            )
            repository.save(pokemonParaSalvar)
            logger.info("Pokemon ${pokemon.nome} salvo com sucesso!")
        } catch (e: Exception) {
            logger.error("Erro ao salvar Pokemon ${pokemon.nome}: ${e.message}")
            throw IllegalStateException("Erro ao salvar no banco de dados.")
        }
    }
}

private fun PokemonApiResponse.toSimplificado(evolucoes: List<String>?): PokemonSimplificado {
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
        stats = statsMapeadas,
        evolucoes = evolucoes
    )
}

class PokemonNotFoundException(message: String) : RuntimeException(message)