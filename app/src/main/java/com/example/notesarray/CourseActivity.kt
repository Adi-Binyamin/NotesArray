package com.example.notesarray

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent


class CourseActivity : AppCompatActivity() {

    private val courseList = listOf(
        "Introduction to Computer Science", "Calculus 1", "Linear Algebra 1", "Discrete Mathematics", "Calculus 2",
        "Probability", "Object-Oriented Programming", "Computer Organization and Assembly", "Introduction to Programming",
        "Mathematical Logic Systems", "Data Structures", "Linear Algebra 2", "Operating Systems",
        "Computer Networks for Software", "Computational Models", "Algorithm Design and Analysis",
        "Parallel and Distributed Computing", "Compilation", "Introduction to Software Engineering", "Advanced Algorithms",
        "Machine Learning"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        val actionType = intent.getStringExtra("action") ?: "search"

        val courseEditText = findViewById<EditText>(R.id.courseNumberEditText)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }


        confirmButton.setOnClickListener {
            val input = courseEditText.text.toString()
            val courseNumber = input.toIntOrNull()

            if (courseNumber == null || courseNumber !in 1..courseList.size) {
                Toast.makeText(this, "Invalid course number", Toast.LENGTH_SHORT).show()
            } else {
                val courseName = courseList[courseNumber - 1]
                val nextIntent = if (actionType == "search") {
                    Intent(this, SearchActivity::class.java)
                } else {
                    Intent(this, UploadActivity::class.java)
                }

                nextIntent.putExtra("selectedCourseName", courseName)
                startActivity(nextIntent)
            }
            }
        }

}
