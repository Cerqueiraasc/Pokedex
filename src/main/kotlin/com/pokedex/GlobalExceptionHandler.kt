package com.pokedex

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(PokemonNotFoundException::class)
    fun hadleNotFound(e: PokemonNotFoundException): ResponseEntity<Map<String, String>> {
        val errorBody = mapOf("error" to (e.message ?: "Não encontrado"))
        return ResponseEntity(errorBody, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleServiceError(e: IllegalStateException): ResponseEntity<Map<String, String>> {
        logger.error("Erro de serviço interno: ${e.message}", e)

        val errorBody = mapOf("error" to (e.message ?: "Erro de serviço indisponível"))
        return ResponseEntity(errorBody, HttpStatus.SERVICE_UNAVAILABLE)
    }
}