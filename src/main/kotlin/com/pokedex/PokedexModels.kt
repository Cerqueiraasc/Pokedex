package com.pokedex

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonSimplificado(
    val nome: String,
    val id: Int,
    @JsonProperty("tipo_principal")
    val tipoPrincipal: String,
    val imagem: String?,
    val altura: Int?,
    val peso: Int?,
    val stats: List<StatSimplificado>?
)

data class StatSimplificado(
    val nome: String,
    val valor: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PokemonApiResponse(
    val id: Int?,
    val name: String?,
    val types: List<TypeSlot>?,
    val sprites: SpriteInfo?,
    val height: Int?,
    val weight: Int?,
    val stats: List<StatSlot>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TypeSlot(
    val slot: Int?,
    val type: TypeInfo?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TypeInfo(
    val name: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpriteInfo(
    @JsonProperty("front_default")
    val frontDefault: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StatSlot(
    @JsonProperty("base_stat")
    val baseStat: Int?,
    val stat: StatInfo?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class StatInfo(
    val name: String?
)