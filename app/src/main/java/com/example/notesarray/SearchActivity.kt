package com.example.notesarray

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesarray.data.Summary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.auth.FirebaseAuth


class SearchActivity : AppCompatActivity() {

    private lateinit var summariesRecyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var openSummaryButton: Button
    private lateinit var addToFavoritesButton: Button
    private lateinit var summaryNumberEditText: EditText
    private lateinit var noSummariesTextView: TextView
    private lateinit var selectionLayout: LinearLayout

    private var summariesList = mutableListOf<Summary>()
    private var selectedCourseName: String? = null

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        selectedCourseName = intent.getStringExtra("selectedCourseName")

        summariesRecyclerView = findViewById(R.id.coursesRecyclerView)
        openSummaryButton = findViewById(R.id.openSummaryButton)
        addToFavoritesButton = findViewById(R.id.addToFavoritesButton)
        backButton = findViewById(R.id.backButton)
        summaryNumberEditText = findViewById(R.id.summaryNumberEditText)
        noSummariesTextView = findViewById(R.id.noSummariesTextView)
        selectionLayout = findViewById(R.id.selectionLayout)

        summariesRecyclerView.layoutManager = LinearLayoutManager(this)

        backButton.setOnClickListener { finish() }

        openSummaryButton.setOnClickListener {
            val input = summaryNumberEditText.text.toString()
            val number = input.toIntOrNull()
            if (number == null || number < 1 || number > summariesList.size) {
                Toast.makeText(this, "מספר לא חוקי", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selected = summariesList[number - 1]
            openSummary(selected)
        }
        openSummaryButton.visibility = Button.INVISIBLE

        addToFavoritesButton.setOnClickListener {
            val input = summaryNumberEditText.text.toString()
            val number = input.toIntOrNull()
            if (number == null || number < 1 || number > summariesList.size) {
                Toast.makeText(this, "מספר לא חוקי", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selected = summariesList[number - 1]
            addToFavorites(selected)
        }

        loadSummariesForCourse()
    }


    private fun loadSummariesForCourse() {
        if (selectedCourseName.isNullOrEmpty()) {
            Toast.makeText(this, "לא נבחר קורס", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("summaries")
            .whereEqualTo("courseName", selectedCourseName)
            .get()
            .addOnSuccessListener { documents: QuerySnapshot ->
                summariesList.clear()
                for (doc in documents) {
                    val summary = doc.toObject(Summary::class.java)
                    summariesList.add(summary)
                }

                if (summariesList.isEmpty()) {
                    noSummariesTextView.visibility = TextView.VISIBLE
                    summariesRecyclerView.visibility = RecyclerView.GONE
                    selectionLayout.visibility = LinearLayout.GONE
                } else {
                    noSummariesTextView.visibility = TextView.GONE
                    summariesRecyclerView.visibility = RecyclerView.VISIBLE
                    selectionLayout.visibility = LinearLayout.VISIBLE
                    summariesRecyclerView.adapter = SummaryAdapter(summariesList) { summary ->
                        openSummary(summary)//פותי סיכום בלחיצה עליו
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "שגיאה בטעינת הסיכומים", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openSummary(summary: Summary) {
        if (summary.fileUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(summary.fileUrl), "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "אין אפליקציה שתומכת ב-PDF", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "לא נמצאה כתובת קובץ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToFavorites(summary: Summary) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "אנא התחבר/י כדי להוסיף למועדפים", Toast.LENGTH_SHORT).show()
            return
        }

        val userFavoritesCollection = firestore.collection("users")
            .document(user.uid)
            .collection("favorites")

        userFavoritesCollection.document(summary.id).set(summary)
            .addOnSuccessListener {
                Toast.makeText(this, "הסיכום נוסף למועדפים", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "שגיאה בהוספה למועדפים: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

}
