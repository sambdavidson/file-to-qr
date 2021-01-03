package com.example.qr.fileList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qr.data.FileChunkManager
import com.example.qr.data.IsFileChunk

class FileListViewModel(val fileChunkManager: FileChunkManager) : ViewModel() {
    val fileLiveData = fileChunkManager.getFileSetList()
}

class FileListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FileListViewModel(
                fileChunkManager = FileChunkManager.getFileChunkManager()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}