package com.udb.eventoscomunitarios.controllers

import com.google.firebase.firestore.FirebaseFirestore
import com.udb.eventoscomunitarios.models.Attendance

class AttendanceController {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("attendance")

    fun confirmAttendance(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "${eventId}_$userId"

        val attendance = Attendance(
            id = documentId,
            eventId = eventId,
            userId = userId,
            status = "confirmed"
        )

        collection.document(documentId)
            .set(attendance)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al confirmar asistencia")
            }
    }

    fun cancelAttendance(
        eventId: String,
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "${eventId}_$userId"

        collection.document(documentId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al cancelar asistencia")
            }
    }

    fun getUserAttendance(
        userId: String,
        onSuccess: (List<Attendance>) -> Unit,
        onError: (String) -> Unit
    ) {
        collection.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val attendanceList = result.toObjects(Attendance::class.java)
                onSuccess(attendanceList)
            }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al obtener asistencias")
            }
    }

    fun isUserAttending(
        eventId: String,
        userId: String,
        onResult: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = "${eventId}_$userId"

        collection.document(documentId)
            .get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al verificar asistencia")
            }
    }
}