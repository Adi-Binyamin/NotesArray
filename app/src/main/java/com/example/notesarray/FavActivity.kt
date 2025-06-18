package com.example.notesarray

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesarray.data.Summary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FavActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deleteButton: Button
    private lateinit var viewButton: Button
    private lateinit var backButton: Button
    private lateinit var summariesAdapter: SummaryAdapter
    private var favoritesList = mutableListOf<Summary>()
    private var selectedSummary: Summary? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var favoritesListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav)

        recyclerView = findViewById(R.id.summariesRecyclerView)
        deleteButton = findViewById(R.id.deleteSummaryButton)
        viewButton = findViewById(R.id.viewSummaryButton)
        backButton = findViewById(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        summariesAdapter = SummaryAdapter(favoritesList) { summary ->
            selectedSummary = summary
            Toast.makeText(this, "נבחר סיכום: ${summary.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = summariesAdapter

        backButton.setOnClickListener { finish() }

        deleteButton.setOnClickListener {
            val summary = selectedSummary
            if (summary == null) {
                Toast.makeText(this, "בחר סיכום למחיקה", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            deleteFavorite(summary)
        }

        viewButton.setOnClickListener {
            val summary = selectedSummary
            if (summary == null) {
                Toast.makeText(this, "בחר סיכום לעיון", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            openSummary(summary)
        }

        loadFavorites()
    }

    private fun loadFavorites() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "אנא התחבר/י כדי לראות מועדפים", Toast.LENGTH_SHORT).show()
            return
        }

        favoritesListener?.remove() // אם קיים משתמש מפעם קודמת בפונקציה, להסיר לפני חדש
        // בפיירבייס- תת אוסף פייבוריט בתוך האוסף יוזר
        favoritesListener = firestore.collection("users")
            .document(user.uid)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "שגיאה בטעינת מועדפים: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    favoritesList.clear()
                    for (doc in snapshot.documents) {
                        val summary = doc.toObject(Summary::class.java)
                        if (summary != null) {
                            favoritesList.add(summary)
                        }
                    }
                    summariesAdapter.notifyDataSetChanged()

                    if (favoritesList.isEmpty()) {
                        Toast.makeText(this, "אין סיכומים מועדפים להצגה", Toast.LENGTH_SHORT).show()
                        recyclerView.visibility = RecyclerView.GONE
                    } else {
                        recyclerView.visibility = RecyclerView.VISIBLE
                    }
                    selectedSummary = null
                }
            }
    }

    private fun deleteFavorite(summary: Summary) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "אנא התחבר/י לפני מחיקה", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(user.uid)
            .collection("favorites")
            .document(summary.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "הסיכום נמחק מהמועדפים", Toast.LENGTH_SHORT).show()
                selectedSummary = null
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "שגיאה במחיקת הסיכום: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openSummary(summary: Summary) {
        if (summary.fileUrl.isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(summary.fileUrl), "application/pdf")//מה להציג
                    flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "אין אפליקציה שתומכת ב-PDF", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "לא נמצאה כתובת קובץ", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        favoritesListener?.remove() // לאפס פעילות של משתמש לפעם הבאה
    }
}
