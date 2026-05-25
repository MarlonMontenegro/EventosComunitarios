package com.udb.eventoscomunitarios.controllers

import com.google.firebase.firestore.FirebaseFirestore
import com.udb.eventoscomunitarios.models.Review

class ReviewController {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("reviews")

    fun addReview(
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val document = collection.document()
        val reviewWithId = review.copy(id = document.id)

        document.set(reviewWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al guardar comentario")
            }
    }

    fun getReviewsByEvent(
        eventId: String,
        onSuccess: (List<Review>) -> Unit,
        onError: (String) -> Unit
    ) {
        collection.whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { result ->
                val reviews = result.toObjects(Review::class.java)
                onSuccess(reviews)
            }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al obtener comentarios")
            }
    }

    fun updateReview(
        review: Review,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        collection.document(review.id)
            .set(review)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al actualizar comentario")
            }
    }

    fun deleteReview(
        reviewId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        collection.document(reviewId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { error ->
                onError(error.message ?: "Error al eliminar comentario")
            }
    }
}