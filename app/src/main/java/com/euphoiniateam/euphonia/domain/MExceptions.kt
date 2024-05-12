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

open class ConvertingException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause)

class WaitForConvertingTimeoutException(
    override val message: String? = null,
): ConvertingException(
    message = message
)

class UnexpectedServerResponseException(
    val code: Int,
    val body: String
) : GenerationException()
