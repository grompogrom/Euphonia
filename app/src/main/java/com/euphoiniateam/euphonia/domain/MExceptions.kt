package com.euphoiniateam.euphonia.domain

open class GenerationException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause)

class WaitForGenerationTimeoutException(
    override val message: String? = null,
) : GenerationException(
    message = message
)

class UnexpectedServerResponse(
    val code: Int,
    val body: String
) : GenerationException()
