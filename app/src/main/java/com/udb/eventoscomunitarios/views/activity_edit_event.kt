package com.udb.eventoscomunitarios.views

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.udb.eventoscomunitarios.Enum.category
import com.udb.eventoscomunitarios.R

class activity_edit_event : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_event)

        val etTitle =
            findViewById<EditText>(R.id.etEventTitle)

        val etDescription =
            findViewById<EditText>(R.id.etEventDescription)

        val etDate =
            findViewById<EditText>(R.id.etEventDate)

        val etTime =
            findViewById<EditText>(R.id.etEventTime)

        val etLocation =
            findViewById<EditText>(R.id.etEventLocation)

        val spinnerCategory =
            findViewById<Spinner>(R.id.spinnerCategory)

        val btnSaveEvent =
            findViewById<Button>(R.id.btnSaveEvent)

        val categories = category.entries.map {
            it.name.replace("_", " ")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        spinnerCategory.adapter = adapter

        val eventId =
            intent.getStringExtra("eventId") ?: ""

        val title =
            intent.getStringExtra("title") ?: ""

        val description =
            intent.getStringExtra("description") ?: ""

        val date =
            intent.getStringExtra("date") ?: ""

        val time =
            intent.getStringExtra("time") ?: ""

        val location =
            intent.getStringExtra("location") ?: ""

        val category =
            intent.getStringExtra("category") ?: ""

        etTitle.setText(title)
        etDescription.setText(description)
        etDate.setText(date)
        etTime.setText(time)
        etLocation.setText(location)

        val categoryPosition =
            categories.indexOf(category)

        if (categoryPosition >= 0) {
            spinnerCategory.setSelection(categoryPosition)
        }

        btnSaveEvent.text = "Actualizar evento"

        btnSaveEvent.setOnClickListener {

            FirebaseFirestore
                .getInstance()
                .collection("events")
                .document(eventId)

                .update(
                    mapOf(
                        "title" to etTitle.text.toString(),
                        "description" to etDescription.text.toString(),
                        "date" to etDate.text.toString(),
                        "time" to etTime.text.toString(),
                        "location" to etLocation.text.toString(),
                        "category" to spinnerCategory.selectedItem.toString()
                    )
                )

                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Evento actualizado",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }

                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Error al actualizar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}