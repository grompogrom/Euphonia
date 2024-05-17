package com.euphoiniateam.euphonia.tools

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun generateDateName(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return currentDateTime.format(formatter)
}
