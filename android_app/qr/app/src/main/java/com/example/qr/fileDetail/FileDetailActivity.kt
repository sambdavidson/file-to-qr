package com.example.qr.fileDetail

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qr.FILE_ID
import com.example.qr.R
import com.example.qr.data.IMAGE_TYPES
import com.example.qr.data.TEXT_TYPES
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import kotlin.math.floor

private const val RWFILE_REQUEST_CODE = 102

class FileDetailActivity : AppCompatActivity() {

    private val fileDetailViewModel by viewModels<FileDetailViewModel> {
        FileDetailViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_detail_activity)

        var currentFileId: String? = null

        /* Connect variables to UI elements. */
        val fileName: TextView = findViewById(R.id.file_name)
        val fileImage: ImageView = findViewById(R.id.file_detail_image)
        val fileBody: TextView = findViewById(R.id.file_body)
        val saveFileButton: Button = findViewById(R.id.save_button)
        val removeFileButton: Button = findViewById(R.id.remove_button)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentFileId = bundle.getString(FILE_ID)
        }

        currentFileId?.let {
            // TODO WHAT IF INCOMPLETE!?
            val currentFile = fileDetailViewModel.getFileForId(it) ?: return
            fileName.text = currentFile.filename

            if (!currentFile.IsComplete()) {
                fileImage.visibility = View.GONE
                fileBody.visibility = View.VISIBLE
                val percent = floor((currentFile.partsFound.toDouble() / currentFile.partsTotal.toDouble()) * 100.0).toInt()
                fileBody.text = "FILE INCOMPLETE:\nScanned ${currentFile.partsFound} parts of ${currentFile.partsTotal}.\n${percent}%"
                saveFileButton.isEnabled = false
            } else {
                val ext = File(currentFile.filename).extension
                when {
                    IMAGE_TYPES.contains(ext) -> {
                        fileImage.visibility = View.VISIBLE
                        fileBody.visibility = View.GONE
                        val data = currentFile.CombineChunks() ?: return
                        val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                        fileImage.setImageBitmap(Bitmap.createBitmap(bmp))

                    }
                    TEXT_TYPES.contains(ext) -> {
                        fileImage.visibility = View.GONE
                        fileBody.visibility = View.VISIBLE
                        fileBody.text = currentFile.CombineChunks()?.toString(Charsets.UTF_8)
                    }
                    else -> {
                        fileImage.visibility = View.GONE
                        fileBody.visibility = View.VISIBLE
                        fileBody.text = getString(R.string.cannot_display_file_warning)
                    }
                }
                saveFileButton.isEnabled = true
                saveFileButton.setOnClickListener {
                    setupPermissions()
                    val dir = writeFileOnInternalStorage(currentFile.filename, currentFile.CombineChunks())
                    Toast.makeText(this, "Saved ${currentFile.filename} to $dir", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            removeFileButton.setOnClickListener {
                fileDetailViewModel.removeFile(currentFile)
                finish()
            }
        }

    }

    private fun writeFileOnInternalStorage(filename: String, sBody: ByteArray?): String {

        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "qr_code_files")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val f = File(dir, filename)
            val s = FileOutputStream(f)
            s.write(sBody)
            s.flush()
            s.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dir.path
    }

    private fun setupPermissions() {
        var permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }

        permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }

    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE), RWFILE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            RWFILE_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need to grant access to read/write files to save a file.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}