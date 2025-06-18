package com.example.notesarray

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notesarray.data.Summary
import com.example.notesarray.data.downloadPdfToInternalStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private val PICK_PDF_CODE = 1000
    private var pdfUri: Uri? = null
    private var selectedCourseName: String? = null

    private lateinit var selectPdfButton: Button
    private lateinit var confirmButton: Button
    private lateinit var backButton: Button
    private lateinit var summaryNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        selectedCourseName = intent.getStringExtra("selectedCourseName")

        selectPdfButton = findViewById(R.id.selectPdfButton)
        confirmButton = findViewById(R.id.confirmButton)
        backButton = findViewById(R.id.backButton)
        summaryNameEditText = findViewById(R.id.summaryNameEditText)

        selectPdfButton.setOnClickListener {
            openFilePicker()
        }

        confirmButton.setOnClickListener {
            uploadSummary()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "בחר קובץ PDF"), PICK_PDF_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_CODE && resultCode == Activity.RESULT_OK && data != null) {
            pdfUri = data.data
            Toast.makeText(this, "קובץ נבחר בהצלחה", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadSummary() {
        val summaryName = summaryNameEditText.text.toString().trim()
        if (summaryName.isEmpty()) {
            Toast.makeText(this, "אנא הזיני שם לסיכום", Toast.LENGTH_SHORT).show()
            return
        }
        if (pdfUri == null) {
            Toast.makeText(this, "אנא בחרי קובץ PDF", Toast.LENGTH_SHORT).show()
            return
        }

        val id = UUID.randomUUID().toString()
        val fileRef = FirebaseStorage.getInstance().reference.child("summaries/$id.pdf")

        // העלאת הקובץ ל-Firebase Storage
        fileRef.putFile(pdfUri!!)
            .addOnSuccessListener {
                // קבלת URL להורדה
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val summary = Summary(
                        id = id,
                        name = summaryName,
                        courseName = selectedCourseName ?: "לא נבחר",
                        fileUrl = uri.toString()
                    )

                    // שמירה במסד הנתונים (Firestore)
                    FirebaseFirestore.getInstance().collection("summaries")
                        .document(id)
                        .set(summary)
                        .addOnSuccessListener {
                            downloadPdfToInternalStorage(this, summary) { downloadedFile ->
                                runOnUiThread {
                                    if (downloadedFile != null) {
                                        Toast.makeText(this, "הסיכום נשמר בהצלחה!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(this, "הורדת הסיכום נכשלה", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "שגיאה בשמירת הסיכום למסד נתונים", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "העלאת הקובץ נכשלה", Toast.LENGTH_SHORT).show()
            }
    }
}
