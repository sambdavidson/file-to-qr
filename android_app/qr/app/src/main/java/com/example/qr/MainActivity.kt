package com.example.qr

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.budiyev.android.codescanner.*
import com.example.qr.data.FileChunkManager
import com.example.qr.data.FileChunkSet
import com.example.qr.data.IsFileChunk
import com.example.qr.fileDetail.FileDetailActivity
import com.example.qr.fileList.FileAdapter
import com.example.qr.fileList.FileListViewModel
import com.example.qr.fileList.FileListViewModelFactory


private const val CAMERA_REQUEST_CODE = 101
const val FILE_ID = "file id"

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    private val fileListViewModel by viewModels<FileListViewModel> {
        FileListViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        codeScanner()
        fileRecyclerView()
    }

    private fun fileRecyclerView() {
        val fileAdapter = FileAdapter{
            fileChunkSet -> fileOnClick(fileChunkSet)
        }

        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.files_recycler_view)
        recyclerView.adapter = fileAdapter

        fileListViewModel.fileLiveData.observe(this) {
            it.let {
                fileAdapter.submitList(it)
                fileAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun fileOnClick(set: FileChunkSet) {
        Log.i("Main", "fileOnClick ${set.fileid}")
        val intent = Intent(this, FileDetailActivity()::class.java)
        intent.putExtra(FILE_ID, set.fileid)
        startActivity(intent)
    }


    private fun codeScanner() {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val tvScanStatus = findViewById<TextView>(R.id.tv_scan_status)

        codeScanner = CodeScanner(this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.TWO_DIMENSIONAL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    Log.i("Main", it.text)
                    tvScanStatus.setTextColor(if (IsFileChunk(it.text)) Color.GREEN else Color.RED)
                    tvScanStatus.text = if (IsFileChunk(it.text)) "OK" else "Error / Try Again"
                    FileChunkManager.getFileChunkManager().addChunkString(it.text)
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera initiation error: ${it.message}")
                }
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need camera dumb dumb.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}