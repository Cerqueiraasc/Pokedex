package com.pokedex

import com.fasterxml.jackson.annotation.JsonProperty

data class PokemonSimplificado(
    val nome: String,
    val id: Int,
    val tipo_principal: String,
    val imagem: String?
)

data class PokemonApiResponse(
    val id: Int,
    val name: String,
    val types: List<TypeSlot>,
    val sprites: SpriteInfo
)

data class TypeSlot(
    val slot: Int,
    val type: TypeInfo
)

data class TypeInfo(
    val name: String
)

data class SpriteInfo(
    @JsonProperty("front_default")
    val frontDefault: String?
)