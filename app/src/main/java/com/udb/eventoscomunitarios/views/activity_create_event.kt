package com.udb.eventoscomunitarios.views

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.udb.eventoscomunitarios.R
import com.udb.eventoscomunitarios.controllers.AuthController
import com.udb.eventoscomunitarios.controllers.EventController
import com.udb.eventoscomunitarios.models.Event
import com.udb.eventoscomunitarios.Enum.category
import java.util.Calendar

class activity_create_event : AppCompatActivity() {

    private val eventController = EventController()
    private val authController = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val etTitle = findViewById<EditText>(R.id.etEventTitle)
        val etDescription = findViewById<EditText>(R.id.etEventDescription)
        val etDate = findViewById<EditText>(R.id.etEventDate)
        val etTime = findViewById<EditText>(R.id.etEventTime)
        val etLocation = findViewById<EditText>(R.id.etEventLocation)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val btnSaveEvent = findViewById<Button>(R.id.btnSaveEvent)

        val categories = category.entries.map { it.name.replace("_", " ") }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        spinnerCategory.adapter = adapter

        etDate.isFocusable = false
        etDate.isClickable = true

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->
                    etDate.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etTime.isFocusable = false
        etTime.isClickable = true

        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()

            TimePickerDialog(
                this,
                { _, hour, minute ->
                    etTime.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnSaveEvent.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val date = etDate.text.toString().trim()
            val time = etTime.text.toString().trim()
            val location = etLocation.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val organizerId = authController.getCurrentUserId() ?: ""

            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = Event(
                title = title,
                description = description,
                date = date,
                time = time,
                location = location,
                category = category,
                organizerId = organizerId
            )

            eventController.createEvent(
                event = event,
                onSuccess = {
                    Toast.makeText(this, "Evento creado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
