package com.udb.eventoscomunitarios.models

data class Review(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val comment: String = "",
    val rating: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)