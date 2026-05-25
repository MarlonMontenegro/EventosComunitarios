package com.udb.eventoscomunitarios.views

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.udb.eventoscomunitarios.R
import com.udb.eventoscomunitarios.controllers.AuthController
import com.udb.eventoscomunitarios.models.User

class activity_login : AppCompatActivity() {

    private lateinit var authController: AuthController
    private lateinit var googleSignInClient: GoogleSignInClient

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authController = AuthController()

        val googleOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleOptions)

        val etEmail = findViewById<EditText>(R.id.etEmailLogin)
        val etPassword = findViewById<EditText>(R.id.etPasswordLogin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogleLogin = findViewById<Button>(R.id.btnGoogleLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authController.loginUser(
                email = email,
                password = password,
                onSuccess = {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, activity_dashboard::class.java))
                    finish()
                },
                onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }

        btnGoogleLogin.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, activity_register::class.java))
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(
                    account.idToken,
                    null
                )

                auth.signInWithCredential(credential)
                    .addOnSuccessListener { result ->

                        val firebaseUser = result.user

                        if (firebaseUser != null) {
                            val user = User(
                                uid = firebaseUser.uid,
                                name = firebaseUser.displayName ?: "Usuario Google",
                                email = firebaseUser.email ?: "",
                                role = "user"
                            )

                            db.collection("users")
                                .document(firebaseUser.uid)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Inicio con Google exitoso",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    startActivity(
                                        Intent(
                                            this,
                                            activity_dashboard::class.java
                                        )
                                    )

                                    finish()
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(
                                        this,
                                        error.message ?: "Error al guardar usuario",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(
                            this,
                            error.message ?: "Error con Firebase Auth",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            } catch (e: ApiException) {
                Toast.makeText(
                    this,
                    "Error al iniciar con Google: ${e.statusCode}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}