package com.udb.eventoscomunitarios.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udb.eventoscomunitarios.R
import com.udb.eventoscomunitarios.controllers.EventController
import com.udb.eventoscomunitarios.models.Event

class activity_events : AppCompatActivity() {

    private val eventController = EventController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        val containerEvents =
            findViewById<LinearLayout>(R.id.containerEvents)

        eventController.getEvents(

            onSuccess = { events ->

                if (events.isEmpty()) {

                    addEmptyMessage(containerEvents)

                } else {

                    events.forEach { event ->
                        addEventCard(containerEvents, event)
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

    private fun addEmptyMessage(container: LinearLayout) {

        val textView = TextView(this)

        textView.text = "No hay eventos disponibles."
        textView.setTextColor(Color.WHITE)
        textView.textSize = 18f

        container.addView(textView)
    }

    private fun addEventCard(
        container: LinearLayout,
        event: Event
    ) {

        val card = LinearLayout(this)

        card.orientation = LinearLayout.VERTICAL
        card.setPadding(28, 28, 28, 28)
        card.setBackgroundColor(
            Color.parseColor("#F4F4F4")
        )

        card.isClickable = true
        card.isFocusable = true

        val title = TextView(this)

        title.text = event.title
        title.setTextColor(
            Color.parseColor("#17182E")
        )

        title.textSize = 22f

        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val date = TextView(this)

        date.text =
            "📅 ${event.date} · ${event.time}"

        date.setTextColor(Color.GRAY)
        date.textSize = 15f

        val location = TextView(this)

        location.text =
            "📍 ${event.location}"

        location.setTextColor(Color.GRAY)
        location.textSize = 15f

        val category = TextView(this)

        category.text =
            event.category.ifEmpty { "General" }

        category.setTextColor(
            Color.parseColor("#5B3CC4")
        )

        category.textSize = 14f

        category.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        category.setPadding(18, 8, 18, 8)

        category.setBackgroundColor(
            Color.parseColor("#E9E1FF")
        )

        val categoryParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        categoryParams.setMargins(
            0,
            18,
            0,
            0
        )

        category.layoutParams = categoryParams

        title.setPadding(0, 0, 0, 10)
        date.setPadding(0, 0, 0, 6)
        location.setPadding(0, 0, 0, 12)

        card.addView(title)
        card.addView(date)
        card.addView(location)
        card.addView(category)

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(0, 0, 0, 24)

        card.layoutParams = params

        card.setOnClickListener {

            val intent = Intent(
                this,
                activity_event_detail::class.java
            )

            intent.putExtra(
                "eventId",
                event.id
            )

            intent.putExtra(
                "title",
                event.title
            )

            intent.putExtra(
                "description",
                event.description
            )

            intent.putExtra(
                "date",
                event.date
            )

            intent.putExtra(
                "time",
                event.time
            )

            intent.putExtra(
                "location",
                event.location
            )

            intent.putExtra(
                "category",
                event.category
            )

            startActivity(intent)
        }

        container.addView(card)
    }
}