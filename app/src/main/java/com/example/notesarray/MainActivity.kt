package com.example.notesarray

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import com.example.notesarray.CourseActivity
import com.example.notesarray.FavActivity



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchButton = findViewById<Button>(R.id.searchButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        val favoritesButton = findViewById<Button>(R.id.favoritesButton)

        searchButton.setOnClickListener {
            val intent = Intent(this, CourseActivity::class.java)
            intent.putExtra("action", "search")
            startActivity(intent)
        }

        uploadButton.setOnClickListener {
            val intent = Intent(this, CourseActivity::class.java)
            intent.putExtra("action", "upload")
            startActivity(intent)
        }

        favoritesButton.setOnClickListener {
            val intent = Intent(this, FavActivity::class.java)
            startActivity(intent)
        }

    }
}
