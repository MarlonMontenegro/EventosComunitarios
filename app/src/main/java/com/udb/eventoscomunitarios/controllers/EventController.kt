package com.udb.eventoscomunitarios.controllers

import com.google.firebase.firestore.FirebaseFirestore
import com.udb.eventoscomunitarios.models.Event

class EventController {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("events")

    fun createEvent(
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val document = collection.document()
        val eventWithId = event.copy(id = document.id)

        document.set(eventWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al crear el evento")
            }
    }

    fun getEvents(
        onSuccess: (List<Event>) -> Unit,
        onError: (String) -> Unit
    ) {
        collection.get()
            .addOnSuccessListener { result ->
                val events = result.toObjects(Event::class.java)
                onSuccess(events)
            }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al obtener eventos")
            }
    }

    fun updateEvent(
        event: Event,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        collection.document(event.id)
            .set(event)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al actualizar evento")
            }
    }

    fun deleteEvent(
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        collection.document(eventId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al eliminar evento")
            }
    }

    fun getEventsByOrganizer(
        organizerId: String,
        onSuccess: (List<Event>) -> Unit,
        onError: (String) -> Unit
    ) {
        collection.whereEqualTo("organizerId", organizerId)
            .get()
            .addOnSuccessListener { result ->
                val events = result.toObjects(Event::class.java)
                onSuccess(events)
            }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al obtener tus eventos")
            }
    }
}