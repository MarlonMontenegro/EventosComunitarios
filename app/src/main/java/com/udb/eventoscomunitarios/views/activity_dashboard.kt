package com.udb.eventoscomunitarios.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.udb.eventoscomunitarios.R
import com.udb.eventoscomunitarios.controllers.AuthController

class activity_dashboard : AppCompatActivity() {

    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        authController = AuthController()

        val btnCreateEvent =
            findViewById<Button>(R.id.btnCreateEvent)

        val btnViewEvents =
            findViewById<Button>(R.id.btnViewEvents)

        val btnMyEvents =
            findViewById<Button>(R.id.btnMyEvents)

        val btnHistory =
            findViewById<Button>(R.id.btnHistory)

        val btnLogout =
            findViewById<Button>(R.id.btnLogout)

        btnCreateEvent.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    activity_create_event::class.java
                )
            )
        }

        btnViewEvents.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    activity_events::class.java
                )
            )
        }

        btnMyEvents.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    activity_my_events::class.java
                )
            )
        }

        btnHistory.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    activity_history::class.java
                )
            )
        }

        btnLogout.setOnClickListener {

            authController.logout()

            val intent = Intent(
                this,
                activity_login::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}