package com.udb.eventoscomunitarios.models

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val category: String = "",
    val organizerId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)