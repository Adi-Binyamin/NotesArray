package com.example.notesarray.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.File

data class Summary(
    var id: String = "",             // UUID שנוצר כששומרים לפיירסטור
    var name: String = "",           // שם הסיכום
    var fileUrl: String = "",        // קישור לקובץ PDF מ-Firebase Storage
    var courseName: String = ""      // שם הקורס
)

// פונקציה להורדת PDF מ-Firebase Storage ושמירתו בזיכרון הפנימי
fun downloadPdfToInternalStorage(context: Context, summary: Summary, onComplete: (File?) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(summary.fileUrl)
    val localFile = File(context.filesDir, "${summary.name}.pdf")

    storageRef.getFile(localFile)
        .addOnSuccessListener {
            onComplete(localFile) // הצליח
        }
        .addOnFailureListener { exception ->
            Log.e("PDFDownload", "הורדת קובץ נכשלה", exception)
            onComplete(null) // נכשל
        }
}
