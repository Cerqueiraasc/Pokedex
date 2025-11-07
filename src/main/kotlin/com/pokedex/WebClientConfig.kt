package com.pokedex

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun pokeApiWebConfig (builder: WebClient.Builder): WebClient {
         return builder
            .baseUrl("https://pokeapi.co/api/v2")
            .build()
    }
}