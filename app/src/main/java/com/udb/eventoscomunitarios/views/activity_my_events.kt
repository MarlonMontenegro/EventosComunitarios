package com.udb.eventoscomunitarios.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.udb.eventoscomunitarios.R
import com.udb.eventoscomunitarios.controllers.AuthController
import com.udb.eventoscomunitarios.controllers.EventController
import com.udb.eventoscomunitarios.models.Event

class activity_my_events : AppCompatActivity() {

    private val authController = AuthController()
    private val eventController = EventController()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_events)

        val btnBack =
            findViewById<Button>(R.id.btnBackMyEvents)

        btnBack.setOnClickListener {
            finish()
        }

        val container =
            findViewById<LinearLayout>(R.id.containerMyEvents)

        val userId =
            authController.getCurrentUserId()

        if (userId == null) {

            Toast.makeText(
                this,
                "Usuario no autenticado",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        eventController.getEventsByOrganizer(

            organizerId = userId,

            onSuccess = { events ->

                if (events.isEmpty()) {

                    val emptyText = TextView(this)

                    emptyText.text =
                        "No has creado eventos."

                    emptyText.setTextColor(Color.WHITE)

                    container.addView(emptyText)

                } else {

                    events.forEach { event ->

                        addEventCard(
                            container,
                            event
                        )
                    }
                }
            },

            onError = { error ->

                Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun addEventCard(
        container: LinearLayout,
        event: Event
    ) {

        val card = LinearLayout(this)

        card.orientation = LinearLayout.VERTICAL

        card.setPadding(
            28,
            28,
            28,
            28
        )

        card.setBackgroundColor(
            Color.parseColor("#F4F4F4")
        )

        val title = TextView(this)

        title.text = event.title

        title.setTextColor(
            Color.parseColor("#17182E")
        )

        title.textSize = 20f

        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val info = TextView(this)

        info.text =
            "📅 ${event.date} · ${event.time}\n📍 ${event.location}"

        info.setTextColor(Color.GRAY)

        info.textSize = 14f

        val btnEdit = Button(this)

        btnEdit.text = "Editar"

        btnEdit.setTextColor(Color.WHITE)

        btnEdit.setBackgroundColor(
            Color.parseColor("#6C3FD1")
        )

        btnEdit.setOnClickListener {

            val intent = Intent(
                this,
                activity_edit_event::class.java
            )

            intent.putExtra("eventId", event.id)
            intent.putExtra("title", event.title)
            intent.putExtra("description", event.description)
            intent.putExtra("date", event.date)
            intent.putExtra("time", event.time)
            intent.putExtra("location", event.location)
            intent.putExtra("category", event.category)

            startActivity(intent)
        }

        val btnDelete = Button(this)

        btnDelete.text = "Eliminar"

        btnDelete.setTextColor(Color.WHITE)

        btnDelete.setBackgroundColor(
            Color.parseColor("#E63946")
        )

        btnDelete.setOnClickListener {

            AlertDialog.Builder(this)

                .setTitle("Eliminar evento")

                .setMessage(
                    "¿Deseas eliminar este evento?"
                )

                .setPositiveButton("Sí") { _, _ ->

                    deleteEvent(event.id)
                }

                .setNegativeButton(
                    "Cancelar",
                    null
                )

                .show()
        }

        title.setPadding(0, 0, 0, 10)

        info.setPadding(0, 0, 0, 20)

        card.addView(title)
        card.addView(info)
        card.addView(btnEdit)
        card.addView(btnDelete)

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            0,
            0,
            0,
            24
        )

        card.layoutParams = params

        container.addView(card)
    }

    private fun deleteEvent(eventId: String) {

        FirebaseFirestore
            .getInstance()
            .collection("events")
            .document(eventId)
            .delete()

            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Evento eliminado",
                    Toast.LENGTH_SHORT
                ).show()

                recreate()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al eliminar",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}