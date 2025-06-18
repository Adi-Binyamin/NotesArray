package com.example.notesarray

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent


class NewSActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var backButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_s)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        backButton = findViewById(R.id.backButton)

        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "אנא הזן אימייל"
                emailEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "אנא הזן סיסמה"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.error = "אנא אמת את הסיסמה"
                confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                confirmPasswordEditText.error = "הסיסמאות אינן תואמות"
                confirmPasswordEditText.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordEditText.error = "הסיסמה חייבת להכיל לפחות 6 תווים"
                passwordEditText.requestFocus()
                return@setOnClickListener
            }

            registerUser(email, password)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "הרשמה הצליחה!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "הרשמה נכשלה: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

}
