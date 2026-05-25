package com.udb.eventoscomunitarios.views

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.udb.eventoscomunitarios.R
import com.udb.eventoscomunitarios.controllers.EventController
import com.udb.eventoscomunitarios.models.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class activity_history : AppCompatActivity() {

    private val eventController = EventController()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val btnBack = findViewById<Button>(R.id.btnBackHistory)
        val containerHistory = findViewById<LinearLayout>(R.id.containerHistory)

        btnBack.setOnClickListener {
            finish()
        }

        eventController.getEvents(
            onSuccess = { events ->
                val pastEvents = events.filter { event ->
                    isPastEvent(event.date)
                }

                if (pastEvents.isEmpty()) {
                    addEmptyMessage(containerHistory)
                } else {
                    pastEvents.forEach { event ->
                        countAttendanceAndAddCard(containerHistory, event)
                    }
                }
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun isPastEvent(date: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val eventDate = formatter.parse(date)
            val today = Date()

            eventDate != null && eventDate.before(today)
        } catch (e: Exception) {
            false
        }
    }

    private fun countAttendanceAndAddCard(container: LinearLayout, event: Event) {
        db.collection("attendance")
            .whereEqualTo("eventId", event.id)
            .get()
            .addOnSuccessListener { result ->
                addHistoryCard(container, event, result.size())
            }
            .addOnFailureListener {
                addHistoryCard(container, event, 0)
            }
    }

    private fun addEmptyMessage(container: LinearLayout) {
        val textView = TextView(this)
        textView.text = "Aún no hay eventos pasados."
        textView.setTextColor(Color.WHITE)
        textView.textSize = 18f
        container.addView(textView)
    }

    private fun addHistoryCard(container: LinearLayout, event: Event, attendanceCount: Int) {
        val card = TextView(this)

        card.text = """
            ${event.title}
            
            📅 ${event.date} · ${event.time}
            📍 ${event.location}
            🏷️ ${event.category.ifEmpty { "General" }}
            
            👥 Personas que asistieron: $attendanceCount
        """.trimIndent()

        card.setTextColor(Color.parseColor("#17182E"))
        card.textSize = 16f
        card.setPadding(28, 28, 28, 28)
        card.setBackgroundColor(Color.parseColor("#F4F4F4"))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 0, 0, 20)
        card.layoutParams = params

        container.addView(card)
    }
}