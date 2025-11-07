package com.pokedex

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonSimplificado(
    val nome: String,
    val id: Int,
    @JsonProperty("tipo_principal")
    val tipoPrincipal: String,
    val imagem: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PokemonApiResponse(
    val id: Int?,
    val name: String?,
    val types: List<TypeSlot>?,
    val sprites: SpriteInfo?
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