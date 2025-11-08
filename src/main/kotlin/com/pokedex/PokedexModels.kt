package com.pokedex

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import jakarta.persistence.Id

data class PokemonSimplificado(
    val nome: String,
    val id: Int,
    val tipoPrincipal: String,
    val imagem: String?,
    val altura: Int?,
    val peso: Int?,
    val stats: List<StatSimplificado>?,
    val evolucoes: List<String>?
)

data class StatSimplificado(
    val nome: String,
    val valor: Int
)

data class PokemonApiResponse(
    val id: Int?,
    val name: String?,
    val types: List<TypeSlot>?,
    val sprites: SpriteInfo?,
    val height: Int?,
    val weight: Int?,
    val stats: List<StatSlot>?,
    val species: SpeciesUrl?
)

data class TypeSlot(
    val slot: Int?,
    val type: TypeInfo?
)

data class TypeInfo(
    val name: String?
)

data class SpriteInfo(
    @JsonProperty("front_default") val frontDefault: String?
)

data class StatSlot(
    @JsonProperty("base_stat") val baseStat: Int?,
    val stat: StatInfo?
)

data class StatInfo(
    val name: String?
)

data class SpeciesUrl(
    val url: String?
)

data class SpeciesApiResponse(
    @JsonProperty("evolution_chain") val evolutionChain: EvolutionChainUrl?
)

data class EvolutionChainUrl(
    val url: String?
)

data class EvolutionChainApiResponse(
    val chain: ChainLink?
)

data class ChainLink(
    val species: SpeciesInfo?,
    @JsonProperty("evolves_to") val evolvesTo: List<ChainLink>?
)

data class SpeciesInfo(
    val name: String?
)

@Entity
@Table(name = "pokemon")
data class PokemonSalvo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column(name = "nome")
    val nome: String,
    @Column(name = "tipo_principal")
    val tipoPrincipal: String?,
    @Column(name = "imagem")
    val imagem: String?
)