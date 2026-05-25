package com.udb.eventoscomunitarios.models

data class Attendance(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val status: String = "confirmed"
)