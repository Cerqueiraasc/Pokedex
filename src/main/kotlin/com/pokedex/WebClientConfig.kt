package com.pokedex

import io.netty.channel.ChannelOption
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig {

    @Bean
    fun pokeApiWebClient(builder: WebClient.Builder): WebClient {

        val httpClient = HttpClient.create()
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000
            )
            .responseTimeout(
                Duration.ofSeconds(10)
            )

        return builder
            .baseUrl("https://pokeapi.co/api/v2")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .defaultHeader(
                HttpHeaders.USER_AGENT, "MinhaPokedex/1.0"
            )
            .build()
    }
}