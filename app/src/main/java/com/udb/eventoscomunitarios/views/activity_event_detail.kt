package com.udb.eventoscomunitarios.views

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.udb.eventoscomunitarios.R
import com.udb.eventoscomunitarios.controllers.AttendanceController
import com.udb.eventoscomunitarios.controllers.AuthController
import com.udb.eventoscomunitarios.controllers.ReviewController
import com.udb.eventoscomunitarios.models.Review

class activity_event_detail : AppCompatActivity() {

    private val authController = AuthController()
    private val attendanceController = AttendanceController()
    private val reviewController = ReviewController()

    private lateinit var containerReviews: LinearLayout
    private lateinit var etReviewComment: EditText
    private lateinit var etReviewRating: EditText

    private var eventId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvDateTime = findViewById<TextView>(R.id.tvDetailDateTime)
        val tvLocation = findViewById<TextView>(R.id.tvDetailLocation)
        val tvDescription = findViewById<TextView>(R.id.tvDetailDescription)

        val btnConfirmAttendance = findViewById<Button>(R.id.btnConfirmAttendance)
        val btnBackToEvents = findViewById<Button>(R.id.btnBackToEvents)
        val btnSaveReview = findViewById<Button>(R.id.btnSaveReview)

        etReviewComment = findViewById(R.id.etReviewComment)
        etReviewRating = findViewById(R.id.etReviewRating)
        containerReviews = findViewById(R.id.containerReviews)

        eventId = intent.getStringExtra("eventId") ?: ""

        val title = intent.getStringExtra("title") ?: "Sin título"
        val description = intent.getStringExtra("description") ?: "Sin descripción"
        val date = intent.getStringExtra("date") ?: "Sin fecha"
        val time = intent.getStringExtra("time") ?: "Sin hora"
        val location = intent.getStringExtra("location") ?: "Sin ubicación"

        tvTitle.text = title
        tvDateTime.text = "📅 $date   🕘 $time"
        tvLocation.text = "📍 $location"
        tvDescription.text = description

        loadReviews()

        btnBackToEvents.setOnClickListener {
            finish()
        }

        btnConfirmAttendance.setOnClickListener {
            val userId = authController.getCurrentUserId()

            if (userId == null || eventId.isEmpty()) {
                Toast.makeText(this, "No se pudo confirmar la asistencia", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            attendanceController.confirmAttendance(
                eventId = eventId,
                userId = userId,
                onSuccess = {
                    Toast.makeText(this, "Asistencia confirmada", Toast.LENGTH_SHORT).show()
                    btnConfirmAttendance.text = "Asistencia confirmada"
                    btnConfirmAttendance.isEnabled = false
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }

        btnSaveReview.setOnClickListener {
            saveReview()
        }
    }

    private fun saveReview() {
        val userId = authController.getCurrentUserId()

        val comment = etReviewComment.text.toString().trim()
        val ratingText = etReviewRating.text.toString().trim()

        if (userId == null || eventId.isEmpty()) {
            Toast.makeText(this, "No se pudo guardar el comentario", Toast.LENGTH_SHORT).show()
            return
        }

        if (comment.isEmpty() || ratingText.isEmpty()) {
            Toast.makeText(this, "Completa comentario y calificación", Toast.LENGTH_SHORT).show()
            return
        }

        val rating = ratingText.toIntOrNull()

        if (rating == null || rating < 1 || rating > 5) {
            Toast.makeText(this, "La calificación debe ser del 1 al 5", Toast.LENGTH_SHORT).show()
            return
        }

        val review = Review(
            eventId = eventId,
            userId = userId,
            comment = comment,
            rating = rating
        )

        reviewController.addReview(
            review = review,
            onSuccess = {
                Toast.makeText(this, "Comentario guardado", Toast.LENGTH_SHORT).show()
                etReviewComment.text.clear()
                etReviewRating.text.clear()
                loadReviews()
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadReviews() {
        if (eventId.isEmpty()) return

        containerReviews.removeAllViews()

        reviewController.getReviewsByEvent(
            eventId = eventId,
            onSuccess = { reviews ->
                if (reviews.isEmpty()) {
                    addEmptyReviewMessage()
                } else {
                    reviews.forEach { review ->
                        addReviewCard(review)
                    }
                }
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun addEmptyReviewMessage() {
        val textView = TextView(this)
        textView.text = "Aún no hay comentarios para este evento."
        textView.setTextColor(Color.WHITE)
        textView.textSize = 16f
        containerReviews.addView(textView)
    }

    private fun addReviewCard(review: Review) {
        val card = TextView(this)

        val stars = "⭐".repeat(review.rating)

        card.text = """
            $stars
            
            ${review.comment}
        """.trimIndent()

        card.setTextColor(Color.WHITE)
        card.textSize = 16f
        card.setPadding(24, 24, 24, 24)
        card.setBackgroundColor(Color.parseColor("#252642"))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 0, 0, 18)
        card.layoutParams = params

        containerReviews.addView(card)
    }
}